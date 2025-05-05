package org.healeasy.services;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.healeasy.config.JwtConfig;
import org.healeasy.entities.User;
import org.healeasy.enums.UserRole;
import org.springframework.stereotype.Component;

import java.util.Date;

@AllArgsConstructor
@Component
public class JwtService {
    private static final Dotenv dotenv = Dotenv.configure().load();
    private final JwtConfig jwtConfig;

    public Jwt generateAccessToken(User user) {
        return generateToken(user, jwtConfig.getAccessTokenExpiration());
    }

    public Jwt generateRefreshToken(User user) {
        return generateToken(user, jwtConfig.getRefreshTokenExpiration());
    }

    private static Jwt generateToken(User user, long tokenExpiration) {
        var claims = Jwts.claims()
                .subject(user.getId().toString())
                .add("username", user.getUsername())
                .add("email", user.getEmail())
                .add("phoneNumber", user.getPhoneNumber())
                .add("role", user.getRole())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * tokenExpiration))
                .build();
        return new Jwt(claims, Keys.hmacShaKeyFor(dotenv.get("JWT_SECRET_KEY").getBytes()));
    }

    public Jwt parseToken(String token){
        try{
            var claims = getClaims(token);
            return new Jwt(claims, Keys.hmacShaKeyFor(dotenv.get("JWT_SECRET_KEY").getBytes()));
        }catch (JwtException e){
            return null;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(dotenv.get("JWT_SECRET_KEY").getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}