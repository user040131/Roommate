package seungjub270.roommate_spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import seungjub270.roommate_spring.domain.RefreshToken;
import seungjub270.roommate_spring.repository.RefreshTokenRepository;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected token"));
    }
}
