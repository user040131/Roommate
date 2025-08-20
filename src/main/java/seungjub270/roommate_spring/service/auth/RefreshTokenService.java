package seungjub270.roommate_spring.service.auth;

import seungjub270.roommate_spring.domain.RefreshToken;

public interface RefreshTokenService {
    void save(RefreshToken token);
    boolean isValid(String token);
    String rotate(String oldToken, String newToken, String username, java.time.Instant newExp);
    void revoke(String token);
    void revokeAllForUser(String username);
}
