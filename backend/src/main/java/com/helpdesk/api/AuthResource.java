package com.helpdesk.api;

import com.helpdesk.security.JwtUtil;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    private static final Map<String, String> USERS = new HashMap<>();
    static {
        USERS.put("admin", "admin123");
        USERS.put("support", "support123");
        USERS.put("user", "user123");
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

        if (USERS.containsKey(c.username) && USERS.get(c.username).equals(c.password)){
            String token = JwtUtil.generateToken(c.username);
            return Response.ok(new Token(token)).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED)
                       .entity("{\"error\":\"Usuário ou senha incorretos.\"}")
                       .build();
    }
}
