package org.healeasy.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.healeasy.enums.UserRole;

import javax.crypto.SecretKey;
import java.util.Date;

@AllArgsConstructor
@Data
public class Jwt {
    private Claims claims;
    private SecretKey secretKey;

    public boolean isExpired(){
        return claims.getExpiration().before(new Date());
    }

    public Long getUserId(){
        return Long.valueOf(claims.getSubject());
    }

    public UserRole getRole(){
        return UserRole.valueOf(claims.get("role", String.class));
    }

    public String toString(){
        return Jwts.builder().claims(claims).signWith(secretKey).compact();
    }
}
