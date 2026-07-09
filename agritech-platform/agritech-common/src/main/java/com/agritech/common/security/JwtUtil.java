package com.agritech.common.security;

import com.agritech.common.constants.AppConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility class for JWT token operations.
 * Handles token generation, validation, and claim extraction.
 *
 * <p>Access Token TTL: 30 minutes. Refresh Token TTL: 7 days.
 * Algorithm: HS512.</p>
 *
 * <p>Each microservice that needs JWT functionality should instantiate
 * this utility with the shared secret key.</p>
 */
@Slf4j
public class JwtUtil {

    private final SecretKey secretKey;

    /**
     * Constructs JwtUtil with a Base64-encoded secret key.
     *
     * @param base64Secret the Base64-encoded HS512 secret key
     */
    public JwtUtil(String base64Secret) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
    }

    /**
     * Generates an access token with custom claims.
     *
     * @param email    the user's email (subject)
     * @param userId   the user's ID
     * @param role     the user's role (e.g., ROLE_FARMER)
     * @param isVerified whether the user is verified
     * @return the signed JWT access token
     */
    public String generateAccessToken(String email, Long userId, String role, boolean isVerified) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);
        claims.put("isVerified", isVerified);
        return buildToken(claims, email, AppConstants.ACCESS_TOKEN_EXPIRY);
    }

    /**
     * Generates a refresh token.
     *
     * @param email the user's email (subject)
     * @return the signed JWT refresh token
     */
    public String generateRefreshToken(String email) {
        return buildToken(new HashMap<>(), email, AppConstants.REFRESH_TOKEN_EXPIRY);
    }

    /**
     * Extracts the subject (email) from a token.
     *
     * @param token the JWT token
     * @return the email/subject
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the user ID from a token.
     *
     * @param token the JWT token
     * @return the user ID
     */
    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Long.class));
    }

    /**
     * Extracts the role from a token.
     *
     * @param token the JWT token
     * @return the role string
     */
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    /**
     * Extracts the verification status from a token.
     *
     * @param token the JWT token
     * @return true if the user is verified
     */
    public boolean extractIsVerified(String token) {
        return extractClaim(token, claims -> claims.get("isVerified", Boolean.class));
    }

    /**
     * Extracts the expiration date from a token.
     *
     * @param token the JWT token
     * @return the expiration date
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts a specific claim using a resolver function.
     *
     * @param token          the JWT token
     * @param claimsResolver function to extract the desired claim
     * @param <T>            the claim type
     * @return the extracted claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Validates a token against an email.
     *
     * @param token the JWT token
     * @param email the expected email
     * @return true if the token is valid and not expired
     */
    public boolean validateToken(String token, String email) {
        try {
            final String extractedEmail = extractEmail(token);
            return extractedEmail.equals(email) && !isTokenExpired(token);
        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Validates a token's structure and signature without checking against a specific user.
     *
     * @param token the JWT token
     * @return true if the token is structurally valid and not expired
     */
    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (ExpiredJwtException e) {
            log.warn("JWT token expired: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token: {}", e.getMessage());
        } catch (SignatureException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Checks if a token has expired.
     *
     * @param token the JWT token
     * @return true if expired
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private String buildToken(Map<String, Object> extraClaims, String subject, long validity) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + validity))
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
