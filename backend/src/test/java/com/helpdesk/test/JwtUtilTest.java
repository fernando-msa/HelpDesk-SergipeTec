package com.helpdesk.test;

import com.helpdesk.security.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilTest {

    @Test
    public void testGenerateAndParse(){
        String token = JwtUtil.generateToken("unit-test-user");
        assertNotNull(token);
        Jws<Claims> parsed = JwtUtil.parse(token);
        assertEquals("unit-test-user", parsed.getBody().getSubject());
    }
}
