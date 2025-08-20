package seungjub270.roommate_spring.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;
import seungjub270.roommate_spring.config.JwtProperties;
import seungjub270.roommate_spring.config.TokenProvider;
import seungjub270.roommate_spring.domain.RefreshToken;
import seungjub270.roommate_spring.service.auth.RefreshTokenService;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final JwtProperties props;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        var authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password())
        ); //id, pw 확인 및 해당 유저의 정보 authentication에 저장
        var user = (UserDetails) authentication.getPrincipal();
        //authentication에 담긴 정보를 user에 다시 담기
        String role = user.getAuthorities().stream() //Collection 타입의 Authority를 stream 타입으로 변환
                .map(GrantedAuthority::getAuthority) //위에서 stream으로 변환된 값을 string으로
                .findFirst().orElse("ROLE_STUDENT").replace("ROLE_", "");


        String access = tokenProvider.createAccessToken(user.getUsername(), role);
        //위에서 뽑아낸 role에 따라서 액세스토큰 발급, 밑에는 리프레시토큰 발급
        String refresh = tokenProvider.createRefreshToken(user.getUsername());

        // DB 저장(회수/회전 추적)
        Instant exp = tokenProvider.getExpiration(refresh);
        // refresh 토큰의 만료시점을 UTC 타임라인의 한 지점을 저장하는 Instant 타입에 저장
        refreshTokenService.save(new RefreshToken(refresh, user.getUsername(), exp));
        //리프레시 토큰, 유저명, 만료시각을 담아서 refreshToken 데이터베이스에 저장

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, refreshCookie(refresh, false).toString());
        //HttpHeader의 쿠키 세팅하기, 그 쿠키에 리프레시토큰 넣기
        return ResponseEntity.ok().headers(headers).body(Map.of("access", access));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request) {
        var cookie = WebUtils.getCookie(request, "refreshToken");
        //요청에서 refreshToken이라는 이름으로 쿠키 뽑아내기
        if (cookie == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        //근데 쿠키가 없으면 인증 받은 적이 없는거니까 UNAUTHORIZED return

        String oldRefresh = cookie.getValue(); //쿠키에 저장된 value(값)을 oldRefresh에 저장
        if (!refreshTokenService.isValid(oldRefresh)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } //해당 리프레시토큰의 유효성 검사, 유효하지 않으면 UNAUTHORIZED

        String username = tokenProvider.getSubject(oldRefresh); // oldRefresh에서 주체 뽑아내기
        String role = tokenProvider.getRole(oldRefresh); // oldRefresh에서 권한 뽑아내기
        // if (role == null) role = "STUDENT"; // 이건 없을 수가 없으니까 삭제

        String newAccess = tokenProvider.createAccessToken(username, role);
        String newRefresh = tokenProvider.createRefreshToken(username);
        Instant newExp = tokenProvider.getExpiration(newRefresh);
        //새로운 access, refresh 토큰 발급, 새 만료기한 지정
        //새로운 값들 refreshTokenService의 rotate(교체)메서드로 저장
        refreshTokenService.rotate(oldRefresh, newRefresh, username, newExp);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, refreshCookie(newRefresh, false).toString());
        //바뀐 리프레시 토큰을 login 메서드와 동일하게 쿠키로 저장해서 return
        return ResponseEntity.ok().headers(headers).body(Map.of("access", newAccess));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        var cookie = WebUtils.getCookie(request, "refreshToken");
        //refresh 메서드와 같이 쿠키 뽑아내기
        if (cookie != null) {
            refreshTokenService.revoke(cookie.getValue());
        } // 쿠키가 제대로 있으면 해당 refreshToken 폐기
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, refreshCookie("deleted", true).toString());
        //현재 저장되어 있는 refreshToken을 없애기 위한 삭제용 쿠키(값 deleted, expire now)를 담아보냄
        //accessToken이 담겨 있는 body에는 logged out이라는 message 남기기
        return ResponseEntity.ok().headers(headers).body(Map.of("message", "logged out"));
    }

    private ResponseCookie refreshCookie(String value, boolean expireNow) {
        ResponseCookie.ResponseCookieBuilder b = ResponseCookie.from("refreshToken", value)
                //value로 들어가는 헤더용 쿠키의 빌더
                .httpOnly(true) //JS로 접근 불가능한 httpOnly 설정
                .secure(false)  //HTTPS에서만 전송할지, 로컬=false, 배포시=true
                .sameSite("Lax")
                // 쿠키를 전송하는 사이트와, 전송받는 사이트의 스킴과 최상위 도메인 기준으로 전송/미전송
                //Strict: 같은 사이트 요청에만 반응
                //Lax: 같은 사이트 요청은 전송, 다른 사이트에서의 탑레벨 GET만 전송
                //None: 모든 컨텍스트에서 전송
                .path("/auth");
                //쿠키를 요청하는 URL의 경로가 auth일 경우에만 쿠키 전송, 노출/오용/CSRF 표면을 줄이는 목적
        return expireNow ? b.maxAge(0).build()
                : b.maxAge(Duration.ofSeconds(props.refreshTtlSeconds())).build();
    } //위의 세 메서드에서 return시 refreshToken을 쿠키에 담기 위해 사용되는 메서드
    //

    public record LoginRequest(String username, String password) {}
    //dto를 간단하게 선언할 수 있게 해주는 record 타입의 선언, login의 parameter로 해당 record 사용
}

