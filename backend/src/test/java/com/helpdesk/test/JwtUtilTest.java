package com.helpdesk.test;

import com.helpdesk.security.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilTest {

    @BeforeAll
    static void setupTestKey() {
        // Inject a test key since JWT_SECRET env var is not available in tests
        JwtUtil.setTestKey("test-secret-key-for-unit-tests-minimum-32-bytes!");
    }

    @Test
    public void testGenerateAndParse(){
        String token = JwtUtil.generateToken("unit-test-user");
        assertNotNull(token);
        Jws<Claims> parsed = JwtUtil.parse(token);
        assertEquals("unit-test-user", parsed.getBody().getSubject());
    }

    @Test
    public void testExpiredToken() {
        // Generate a token that expired 1 hour ago
        Date pastExpiration = new Date(System.currentTimeMillis() - 3600_000);
        String token = JwtUtil.generateToken("expired-user", pastExpiration);
        assertThrows(ExpiredJwtException.class, () -> JwtUtil.parse(token));
    }

    @Test
    public void testInvalidToken() {
        assertThrows(JwtException.class, () -> JwtUtil.parse("not-a-valid-token"));
    }

    @Test
    public void testTamperedToken() {
        String token = JwtUtil.generateToken("test-user");
        // Flip a character in the middle of the token
        char[] chars = token.toCharArray();
        int mid = chars.length / 2;
        chars[mid] = chars[mid] == 'A' ? 'B' : 'A';
        String tampered = new String(chars);
        assertThrows(JwtException.class, () -> JwtUtil.parse(tampered));
    }

    @Test
    public void testTokenContainsIssuedAt() {
        String token = JwtUtil.generateToken("iat-user");
        Jws<Claims> parsed = JwtUtil.parse(token);
        assertNotNull(parsed.getBody().getIssuedAt(), "Token should contain issuedAt claim");
        assertTrue(parsed.getBody().getIssuedAt().getTime() > 0, "IssuedAt should be a positive timestamp");
    }
}
