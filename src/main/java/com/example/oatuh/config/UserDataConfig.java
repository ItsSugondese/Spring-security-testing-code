package com.example.oatuh.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class UserDataConfig {

    private final JwtUtil jwtUtil;


    public String getEmailFromToken(String token) {
        return jwtUtil.extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return jwtUtil.extractClaim(token, Claims::getExpiration);
    }
    public Integer userId(String token){
        Claims claims = Jwts.parser()
                .setSigningKey("secret")
                .parseClaimsJws(token)
                .getBody();
        return (Integer) claims.get("userId");
    }
}
