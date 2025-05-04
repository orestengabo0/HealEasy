package org.healeasy.security;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.healeasy.config.JwtConfig;
import org.healeasy.entities.User;
import org.springframework.stereotype.Component;

import java.util.Date;

@AllArgsConstructor
@Component
public class JwtTokenProvider {
    private static final Dotenv dotenv = Dotenv.configure().load();
    private final JwtConfig jwtConfig;

    public String generateAccessToken(User user) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + jwtConfig.getAccessTokenExpiration());

        return generateToken(user, now, validity);
    }

    public String generateRefreshToken(User user) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + jwtConfig.getRefreshTokenExpiration());

        return generateToken(user, now, validity);
    }

    private static String generateToken(User user, Date now, Date validity) {
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("username", user.getUsername())
                .claim("email", user.getEmail())
                .claim("phoneNumber", user.getPhoneNumber())
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(Keys.hmacShaKeyFor(dotenv.get("JWT_SECRET_KEY").getBytes()))
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            var claims = getClaims(token);
            return claims.getExpiration().after(new Date());
        }catch (JwtException ex){
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(dotenv.get("JWT_SECRET_KEY").getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long getUserIdFromToken(String token) {
        return Long.valueOf(getClaims(token).getSubject());
    }
}