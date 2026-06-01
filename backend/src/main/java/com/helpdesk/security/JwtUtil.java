package com.helpdesk.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtil {
    private static Key key;

    static {
        String secret = System.getenv("JWT_SECRET");
        if (secret != null && !secret.isBlank()) {
            if (secret.getBytes().length < 32) {
                throw new IllegalStateException("JWT_SECRET must be at least 32 bytes (got "
                        + secret.getBytes().length + "). Generate one with: openssl rand -base64 48");
            }
            key = Keys.hmacShaKeyFor(secret.getBytes());
        }
    }

    /** Allows tests to inject a key when JWT_SECRET env var is not available. */
    public static void setTestKey(String secret) {
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    private static Key getKey() {
        if (key == null) {
            throw new IllegalStateException("JWT_SECRET environment variable is not set. "
                    + "Generate one with: openssl rand -base64 48");
        }
        return key;
    }

    public static String generateToken(String subject){
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + 3600_000))
                .signWith(getKey())
                .compact();
    }

    /** Generates a token with a custom expiration (for testing expired tokens). */
    public static String generateToken(String subject, Date expiration){
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(expiration)
                .signWith(getKey())
                .compact();
    }

    public static Jws<Claims> parse(String token){
        return Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token);
    }
}
