package org.example.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class JwtAuthenticationService {

    private Algorithm signInAlgorithm;

    private JWTVerifier jwtVerifier;

    public JwtAuthenticationService() {
        var secret = System.getenv("JWT_SECRET");
        this.signInAlgorithm = Algorithm.HMAC256(secret.getBytes());
        this.jwtVerifier = JWT.require(signInAlgorithm).withIssuer("shortLink").build();
    }

    public String getToken(String id) {
        var issuedAt = Instant.now();
        var expiresAt = issuedAt.plus(24, ChronoUnit.HOURS);
        return JWT.create().withClaim("userId", id).withIssuedAt(issuedAt).withExpiresAt(expiresAt).withIssuer("shortLink").sign(signInAlgorithm);
    }

    public boolean isValid(String token) {
        try {
            jwtVerifier.verify(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public String getUserIdFromToken(String token) {
        var decodedJwt = JWT.decode(token);
        return decodedJwt.getClaim("userId").asString();
    }
}
