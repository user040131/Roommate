package seungjub270.roommate_spring.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

public class TokenProvider {

    private final JwtProperties props;
    private final SecretKey key;

    public TokenProvider(JwtProperties props) {
        this.props = props;
        this.key = Keys.hmacShaKeyFor(props.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(String subject, String role) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(props.accessTtlSeconds());
        return Jwts.builder()
                .setIssuer(props.issuer())
                .setSubject(subject)
                .addClaims(Map.of("role", role)) // 예: STUDENT / MANAGER
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(String subject) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(props.refreshTtlSeconds());
        return Jwts.builder()
                .setIssuer(props.issuer())
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .requireIssuer(props.issuer())
                .build()
                .parseClaimsJws(token);
    }

    public String getSubject(String token) {
        return parse(token).getBody().getSubject();
    }

    public String getRole(String token) {
        Object v = parse(token).getBody().get("role");
        return v == null ? null : v.toString();
    }

    public Instant getExpiration(String token) {
        Date d = parse(token).getBody().getExpiration();
        return d.toInstant();
    }
}
