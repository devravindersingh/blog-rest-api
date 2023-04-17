package com.ravinder.project.blog.security;

import com.ravinder.project.blog.exception.BlogAPIException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${app.jwt-secret}")
    private String jwtSecret;
    @Value("${app.jwt-expiration-milliseconds}")
    private Long jwtExpirationDate;

    //generate Jwt token
    public String generateJwtToken(Authentication authentication){
        String usernameOrEmail = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);
        String token = Jwts.builder()
                .setSubject(usernameOrEmail)
                .setIssuedAt(currentDate)
                .setExpiration(expireDate)
                .signWith(getSecretKey())
                .compact();
        return token;
    }

    private Key getSecretKey(){
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(jwtSecret)
        );
    }

    //get username from Jwt token
    public String getUsernameFromJwtToken(String jwtToken){
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();
        String usernameOrEmail = claims.getSubject();
        return usernameOrEmail;
    }

    //validate Jwt token
    public boolean validateJwtToken(String jwtToken){
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build().parse(jwtToken);
            return true;
        } catch (MalformedJwtException e) {
            throw new BlogAPIException(HttpStatus.BAD_REQUEST,"Invalid Jwt token");
        } catch (ExpiredJwtException e) {
            throw new BlogAPIException(HttpStatus.BAD_REQUEST,"Expired Jwt token");
        } catch (UnsupportedJwtException e) {
            throw new BlogAPIException(HttpStatus.BAD_REQUEST,"Unsupported Jwt token");
        } catch (IllegalArgumentException e) {
            throw new BlogAPIException(HttpStatus.BAD_REQUEST,"Jwt claims string is empty");
        }
    }
}
