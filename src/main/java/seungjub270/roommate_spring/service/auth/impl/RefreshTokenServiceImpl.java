package seungjub270.roommate_spring.service.auth.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seungjub270.roommate_spring.domain.RefreshToken;
import seungjub270.roommate_spring.repository.RefreshTokenRepository;
import seungjub270.roommate_spring.service.auth.RefreshTokenService;
import seungjub270.roommate_spring.repository.RefreshTokenRepository;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository repo;

    @Override @Transactional
    public void save(RefreshToken token) {
        repo.save(token);
    }

    @Override @Transactional(readOnly = true)
    public boolean isValid(String token) {
        return repo.findById(token)
                .filter(t -> !t.isRevoked())
                .filter(t -> t.getExpiredAt().isAfter(Instant.now()))
                .isPresent();
    }

    @Override @Transactional
    public String rotate(String oldToken, String newToken, String username, Instant newExp) {
        repo.findById(oldToken).ifPresent(t -> t.revoke());
        repo.save(new RefreshToken(newToken, username, newExp));
        return newToken;
    }

    @Override @Transactional
    public void revoke(String token) {
        repo.findById(token).ifPresent(RefreshToken::revoke);
    }

    @Override @Transactional
    public void revokeAllForUser(String username) {
        repo.findAllByUsername(username).forEach(RefreshToken::revoke);
    }
}
