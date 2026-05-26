package com.fullstack.gstbillingwithinvoicemngmt.util;

import com.fullstack.gstbillingwithinvoicemngmt.dto.UserResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JWTUtil {
    /**
     * The secret is used encrypt or decrypt token
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * The token expiration time in integer value
     */
    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * Key Generation
     *
     * @return {@link SecretKey}
     */
    private SecretKey getSignKey() {
        byte[] bytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(bytes);
    }

    /**
     * Pass the jwt token this method will give exact username
     *
     * @param token JWT token
     * @return username
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Pass the jwt token this method will give expiration date
     *
     * @param token JWT token
     * @return expiration date
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Method providing claim
     *
     * @param token          JWT token
     * @param claimsResolver {@link Function}
     * @param <T>            return type
     * @return expected value such as subject or expiration date
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * For getting any data from token secret key will require
     *
     * @param token JWT token
     * @return {@link Claims}
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(getSignKey()).build().parseSignedClaims(token).getPayload();
    }

    /**
     * Check if token expired
     *
     * @param token JWT token
     * @return boolean value
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * For generating token
     *
     * @param user {@link UserResponse}
     * @return JWT token
     */
    public String generateToken(UserResponse user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("Name", user.fullName());
        claims.put("Email", user.email());
        return createToken(claims, user.userName());
    }

    /**
     * Helper method for creating token
     *
     * @param claims  map of claims
     * @param subject username or email
     * @return token
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts
                .builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignKey(), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Validating token
     *
     * @param token       JWT token
     * @param userDetails {@link UserDetails}
     * @return boolean result
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUsername(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
