package com.security.apisecurity.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.nio.charset.StandardCharsets;

@Component
public class JwtUtil {

    // IMPORTANTE: idealmente trocar por variável de ambiente (não deixar segredo hardcoded)
    private final String secret = "muito-long-secret-para-jwt-32-bytes-minimo-exemplo-2025-!!";
    private final Key key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    private final long expirationMs = 24 * 60 * 60 * 1000L; // 24h

    public String generateToken(String username) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
