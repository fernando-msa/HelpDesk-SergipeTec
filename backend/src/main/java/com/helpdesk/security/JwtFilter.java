package com.helpdesk.security;

import io.jsonwebtoken.JwtException;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.util.logging.Logger;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class JwtFilter implements ContainerRequestFilter {

    private static final Logger LOG = Logger.getLogger(JwtFilter.class.getName());

    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public void filter(ContainerRequestContext ctx) {
        // Deixa o endpoint de login passar sem token
        String path = ctx.getUriInfo().getPath();
        if (path.startsWith("api/auth")) {
            return;
        }

        String authHeader = ctx.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            abort(ctx, "Token de autenticação ausente.");
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length()).trim();

        try {
            JwtUtil.parse(token); // valida assinatura e expiração
        } catch (JwtException | IllegalArgumentException e) {
            LOG.warning("Invalid or expired JWT token: " + e.getMessage());
            abort(ctx, "Token inválido ou expirado.");
        }
    }

    private void abort(ContainerRequestContext ctx, String message) {
        ctx.abortWith(
            Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\":\"" + message + "\"}")
                    .build()
        );
    }
}
