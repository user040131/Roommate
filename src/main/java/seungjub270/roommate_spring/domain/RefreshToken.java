package seungjub270.roommate_spring.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
public class RefreshToken {
    @Id
    @Column(length = 255)
    private String token;           // 토큰 자체를 PK로 사용(해시 저장도 가능)

    @Column(nullable = false, length = 100)
    private String username;        // 소유자(로그인 아이디)

    @Column(nullable = false)
    private Instant expiredAt;

    @Column(nullable = false)
    private boolean revoked = false;

    protected RefreshToken() {}

    public RefreshToken(String token, String username, Instant expiredAt) {
        this.token = token;
        this.username = username;
        this.expiredAt = expiredAt;
    }

    public String getToken() { return token; }
    public String getUsername() { return username; }
    public Instant getExpiredAt() { return expiredAt; }
    public boolean isRevoked() { return revoked; }
    public void revoke() { this.revoked = true; }

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
}
