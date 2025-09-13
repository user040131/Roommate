package seungjub270.roommate_spring.config;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;
import seungjub270.roommate_spring.service.TokenService;

import java.io.IOException;

@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;
    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer ";
    private final TokenService tokenService;
    private final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    private final String REFRESH_TOKEN_COOKIE_PATH = "/";

    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie refreshCookie = WebUtils.getCookie(request, "refreshToken");
        return refreshCookie != null ? refreshCookie.getValue() : null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);
        String accessToken = getAccessToken(authorizationHeader);
//        try {
//            if (tokenProvider.validToken(token)) {
//                Authentication authentication = tokenProvider.getAuthentication(token);
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//                //accessToken 유효할 시 SecurityContextHolder 세팅하는 코드
//            }
//        } catch (ExpiredJwtException e) { //재발급해야됨
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.setContentType("appliation/json;charset=utf-8");
//            response.getWriter().write("{\"code\":\"ACCESS_TOKEN_EXPIRED\",\"message\":\"Access token expired\"}");
//        } catch (JwtException | IllegalArgumentException e) { //변조됨, 큰일남
//
//        }
        try {
            if (tokenProvider.validToken(accessToken)) {
                Authentication authentication = tokenProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                //accessToken 유효할 시 SecurityContextHolder 세팅하는 코드
            }
        } catch (ExpiredJwtException e) { //만료시
            String refreshToken = getRefreshTokenFromCookie(request);
            if(tokenProvider.validToken(refreshToken)) {
                String newAccessToken = tokenService.createNewAccessToken(refreshToken);
                Authentication authentication = tokenProvider.getAuthentication(newAccessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else { //refreshToken이 만료되었을 때
                forceLogoutWith401(response);
                return;
            }
        } catch (JwtException | IllegalArgumentException e) { //refreshToken이 손상되었을 때
            forceLogoutWith401(response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getAccessToken(String authorizationHeader) {
        if(authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
            return authorizationHeader.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    private void forceLogoutWith401(HttpServletResponse httpServletResponse) throws IOException {
        // 1) SecurityContext 비우기
        SecurityContextHolder.clearContext();

        // 2) refreshToken 쿠키 즉시 삭제
        String deleteCookie = REFRESH_TOKEN_COOKIE_NAME + "=; Path=" + REFRESH_TOKEN_COOKIE_PATH
                + "; HttpOnly; Max-Age=0; SameSite=Lax";
        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, deleteCookie);

        // 3) 401 + 간단한 바디
        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        httpServletResponse.getWriter().write("{\"code\":\"LOGOUT\",\"message\":\"Please sign in again\"}");
    }

}
