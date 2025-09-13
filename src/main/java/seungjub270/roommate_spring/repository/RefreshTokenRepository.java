package seungjub270.roommate_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import seungjub270.roommate_spring.domain.RefreshToken;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByUserId(Long userId);
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
