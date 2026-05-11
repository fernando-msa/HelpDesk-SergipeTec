package com.helpdesk.api;

import com.helpdesk.security.JwtUtil;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    public static class Credentials { public String username; public String password; }
    public static class Token { public String token; public Token(String t){this.token=t;} }

    @POST
    @Path("login")
    public Response login(Credentials c){
        // demo: replace with real auth
        if ("admin".equals(c.username) && "password".equals(c.password)){
            String token = JwtUtil.generateToken(c.username);
            return Response.ok(new Token(token)).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }
}
