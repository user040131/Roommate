package seungjub270.roommate_spring.config;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import seungjub270.roommate_spring.config.jwt.JwtProperties;
import seungjub270.roommate_spring.domain.Account;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class TokenProvider {

    private final JwtProperties jwtProperties;

    public String generateToken(Account account, Duration expiredAt){
        Date now  = new Date();
        return makeToken(new Date(now.getTime() + expiredAt.toMillis()), account);
    }

    private String makeToken(Date expiry, Account account) {
        Date now  = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .setSubject(account.getEmail())
                .claim("id", account.getId())
                .claim("tokenType", "accessToken")
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecret())
                .compact();
    }

    public boolean validToken(String token)
            throws ExpiredJwtException, IllegalArgumentException {
            Jwts.parserBuilder()
                    .setSigningKey(jwtProperties.getSecret())
                    .build()
                    .parseClaimsJws(token);
            return true;
    }

    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

        return new UsernamePasswordAuthenticationToken(new org.springframework.security.core.userdetails.User(
                claims.getSubject(), "", authorities), token, authorities);
    }

    public Long getUserId(String token){
        Claims claims = getClaims(token);
        return claims.get("id", Long.class);
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecret())
                .parseClaimsJws(token)
                .getBody();
    }

    public String createRefreshToken(Account account) {

        Date now  = new Date();

        return Jwts.builder()
                .setSubject(String.valueOf(account.getId()))
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + Duration.ofDays(7).toMillis()))
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecret())
                .claim("tokenType", "refreshToken")
                .compact();
    }
}
