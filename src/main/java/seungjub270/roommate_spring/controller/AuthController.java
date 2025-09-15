package seungjub270.roommate_spring.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import seungjub270.roommate_spring.config.TokenProvider;
import seungjub270.roommate_spring.domain.Account;
import seungjub270.roommate_spring.domain.Auth;
import seungjub270.roommate_spring.domain.RefreshToken;
import seungjub270.roommate_spring.dto.*;
import seungjub270.roommate_spring.repository.AccountRepository;
import seungjub270.roommate_spring.repository.RefreshTokenRepository;
import seungjub270.roommate_spring.service.AuthService;
import seungjub270.roommate_spring.service.TokenService;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AccountRepository accountRepository;

    //accessToken 검사할 때 만료되어 있으면 refreshtoken 기준으로 다시 만드는 메서드
    @PostMapping("/api/acsToken")
    public ResponseEntity<CreateAccessTokenResponse> createAccessToken(@RequestBody CreateAccessTokenRequest req) {
        String newAccessToken = tokenService.createNewAccessToken(req.getRefreshToken());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CreateAccessTokenResponse(newAccessToken));
    }

    //accesstoken 검사하고 refreshToken 다시 만들라고 했는데 refreshToken도 만료되어 있으면 진행되는 메서드
    //근데 그러면 accessToken이랑 refreshToken 둘 다 만들어야하는거 아닌가?
    @PostMapping("/api/refToken")
    public ResponseEntity<CreateRefreshTokenResponse> creteRefreshToken(@RequestBody CreateRefreshTokenRequest req) {

        refreshTokenRepository.findByRefreshToken(req.getRefreshToken())
                .orElseThrow(() -> new IllegalArgumentException("Unexcepted refresh token"));
        CreateRefreshTokenResponse res = new CreateRefreshTokenResponse(tokenService.createNewRefreshToken(req.getEmail()));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(res);
        //refreshToken, accessToken 저장방식 다시 한 번 고려하고 return 방식 생각하기
    }

//    @PostMapping("/login")
//    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest req) {
//        try{
//            TokenResponse tokenResponse = authService.login(req);
//            return ResponseEntity.ok(tokenResponse);
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("form", new LoginRequest());
        return "/auth/login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("form") LoginRequest req, HttpServletResponse res, RedirectAttributes ra) {

        try{
            TokenResponse token = authService.login(req);
            //액세스 토큰 생성 및 세부설정
            ResponseCookie access = ResponseCookie.from("accessToken", token.getAccessToken())
                    .httpOnly(true)
                    .secure(false)
                    .sameSite("Lax")
                    .path("/")
                    .maxAge(Duration.ofHours(2))
                    .build();
            //리프레시 토큰 생성 및 세부설정
            ResponseCookie refresh = ResponseCookie.from("refreshToken", token.getRefreshToken())
                    .httpOnly(true)
                    .secure(false)
                    .sameSite("Lax")
                    .path("/auth")
                    .maxAge(Duration.ofDays(4))
                    .build();
            //근데 세부설정을 여기서 하는게 맞냐?
            //.addHeader에 넣고 return
            res.addHeader("Set-Cookie", access.toString());
            res.addHeader("Set-Cookie", refresh.toString());

            if(accountRepository.findByEmail(req.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid email"))
                    .getAuth() == Auth.Manager){
                return "redirect:/manager/main";
            } else {
                return "redirect:/student/main";
            }
        } catch (Exception e){
            ra.addFlashAttribute("loginError", "이메일 또는 비밀번호가 옳지 않습니다.");
            return "redirect:/auth/login";
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request, response,
                SecurityContextHolder.getContext().getAuthentication());
        return ResponseEntity.noContent().build(); //로그아웃 후에 로그인 창으로 redirect
    }

    @PostMapping("/signup/manager")
    public boolean signUpManager (@RequestBody ManagerSignUpRequest req) {
        try {
            authService.makeManager(req);
            return true;
        } catch (Exception e) {
            System.out.println("매니저 생성 중 오류발생" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @PostMapping("/signup/student")
    public boolean signUpStudent (@RequestBody StudentSignUpRequest req) {
        try {
            authService.makeStudent(req);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

