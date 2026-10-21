package edu.cibertec.platon.gateway.auth.security;

import edu.cibertec.platon.gateway.auth.dto.AuthAccountUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final String jwtSecretKey;
    private final long validityInMs;
    private SecretKey secretKey;

    public JwtTokenProvider(
            @Value("${jwt.secret-key}") String jwtSecretKey,
            @Value("${jwt.validity-in-ms}") long validityInMs) {
        this.jwtSecretKey = jwtSecretKey;
        this.validityInMs = validityInMs;
    }

    @PostConstruct
    void init() {
        String secret = Base64.getEncoder().encodeToString(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(AuthAccountUser user) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMs);

        return Jwts.builder()
                .subject(user.username())
                .claim("idUser", user.idUser())
                .claim("email", user.email())
                .claim("idRole", user.idRole())
                .issuedAt(now)
                .expiration(validity)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        return new UsernamePasswordAuthenticationToken(claims.getSubject(), token, Collections.emptyList());
    }

    public boolean isValidToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
