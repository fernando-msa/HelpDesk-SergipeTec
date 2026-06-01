package com.helpdesk.api;

import com.helpdesk.security.JwtUtil;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    private static final Logger LOG = Logger.getLogger(AuthResource.class.getName());
    private static final Map<String, String> USERS = new HashMap<>();

    static {
        // Pre-hashed demo passwords (PBKDF2WithHmacSHA256, 65536 iterations, 256-bit key)
        USERS.put("admin", hashPassword("admin123"));
        USERS.put("support", hashPassword("support123"));
        USERS.put("user", hashPassword("user123"));
    }

    public static class Credentials { public String username; public String password; }
    public static class Token { public String token; public Token(String t){this.token=t;} }

    @POST
    @Path("login")
    public Response login(Credentials c){
        if (c == null || c.username == null || c.password == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"error\":\"Credenciais inválidas.\"}")
                           .build();
        }

        String storedHash = USERS.get(c.username);
        if (storedHash != null && verifyPassword(c.password, storedHash)) {
            String token = JwtUtil.generateToken(c.username);
            LOG.info("Login successful for user: " + c.username);
            return Response.ok(new Token(token)).build();
        }
        LOG.warning("Failed login attempt for user: " + c.username);
        return Response.status(Response.Status.UNAUTHORIZED)
                       .entity("{\"error\":\"Usuário ou senha incorretos.\"}")
                       .build();
    }

    // --- PBKDF2 Password Hashing (no external dependencies) ---

    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;

    static String hashPassword(String password) {
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        byte[] hash = pbkdf2(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        return ITERATIONS + ":" + Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
    }

    static boolean verifyPassword(String password, String stored) {
        String[] parts = stored.split(":");
        if (parts.length != 3) return false;
        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = Base64.getDecoder().decode(parts[1]);
        byte[] expectedHash = Base64.getDecoder().decode(parts[2]);
        byte[] actualHash = pbkdf2(password.toCharArray(), salt, iterations, expectedHash.length * 8);
        return constantTimeEquals(expectedHash, actualHash);
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLength) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
            byte[] hash = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
            spec.clearPassword();
            return hash;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("PBKDF2 not available", e);
        }
    }

    private static boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a.length != b.length) return false;
        int diff = 0;
        for (int i = 0; i < a.length; i++) {
            diff |= a[i] ^ b[i];
        }
        return diff == 0;
    }
}
