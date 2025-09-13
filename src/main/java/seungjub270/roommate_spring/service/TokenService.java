package seungjub270.roommate_spring.service;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import seungjub270.roommate_spring.config.TokenProvider;
import seungjub270.roommate_spring.config.jwt.JwtFactory;
import seungjub270.roommate_spring.domain.Account;
import seungjub270.roommate_spring.repository.AccountRepository;
import seungjub270.roommate_spring.repository.RefreshTokenRepository;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class TokenService {
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final AccountRepository accountRepository;

    public String createNewAccessToken(String refreshToken) {
        if(!tokenProvider.validToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        Long userId = refreshTokenService.findByRefreshToken(refreshToken).getUserId();
        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("access Account not found"));

        return tokenProvider.generateToken(account, Duration.ofHours(2));
        //첫 argument로 들어가는 놈은 무조건 extends UserDetails를 한 놈만 됨
    }

    public String createNewRefreshToken(String email) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("refresh Account not found"));
        return tokenProvider.createRefreshToken(account);

    }
}
