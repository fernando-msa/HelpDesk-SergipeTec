package com.helpdesk.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtil {
    private static final byte[] SECRET = System.getenv().getOrDefault("JWT_SECRET","change-this-secret-to-a-strong-key").getBytes();
    private static final Key key = Keys.hmacShaKeyFor(SECRET);

    public static String generateToken(String subject){
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + 3600_000))
                .signWith(key)
                .compact();
    }

    public static Jws<Claims> parse(String token){
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }
}
