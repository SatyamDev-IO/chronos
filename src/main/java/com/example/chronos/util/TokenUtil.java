package com.example.chronos.util;

import com.example.chronos.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

import java.security.Key;
import java.util.Date;
import java.util.List;

public class TokenUtil {

    private static final String SECRET_KEY ="secretKeyTaskMasterSecretKeyTaskMasterSecretKeyTaskMasterSecretKeyTaskMaster";

    private static final Key KEY = Keys.hmacShaKeyFor(
            SECRET_KEY.getBytes()
    );

    public static String generateJwtToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 8 * 60 * 60 * 1000))
                .claim("roles", List.of("ROLE_" + user.getRole()))
                .signWith(KEY)
                .compact();
    }

    public static Claims validateSignedToken(String jwtToken) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(jwtToken)
                    .getBody();

            System.out.print("Claims: " + claims);
            return claims;
        } catch (SignatureException exception) {
            System.err.println("Invalid JWT signature: " + exception.getMessage());
            return null;
        } catch (io.jsonwebtoken.ExpiredJwtException exception) {
            System.err.println("JWT token is expired: " + exception.getMessage());
            return null;
        } catch (io.jsonwebtoken.MalformedJwtException exception) {
            System.err.println("Invalid JWT token: " + exception.getMessage());
            return null;
        } catch (io.jsonwebtoken.UnsupportedJwtException exception) {
            System.err.println("JWT token is unsupported: " + exception.getMessage());
            return null;
        } catch (IllegalArgumentException exception) {
            System.err.println("JWT claims string is empty: " + exception.getMessage());
            return null;
        }
    }
}