package com.blackcoffee.shopapp.utils;

import com.blackcoffee.shopapp.exception.InvalidParamsException;
import com.blackcoffee.shopapp.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtTokenUtils {
    @Value("${jwt.expiration}")
    private int expiration;
    @Value("${jwt.secretKey}")
    private String secretKey;

    public String generateToken(User user) throws InvalidParamsException {
        //properties-> claims
        Map<String, Object> claims= new HashMap<>();
        claims.put("phoneNumber",user.getPhoneNumber());
        claims.put("email",user.getEmail());
        claims.put("userId",user.getId());
        try{
            String token=Jwts.builder()
                    .setClaims(claims)
                    .setSubject(user.getPhoneNumber())
                    .setExpiration(new Date(System.currentTimeMillis()+expiration*1000L))
                    .signWith(getSignInKey(),SignatureAlgorithm.HS512)
                    .compact();

            return token;
        }catch(Exception e){
            throw new InvalidParamsException("Cannot read token, "+e.getMessage());

        }
    }

    private Key getSignInKey() {
//        byte[] bytes= Decoders.BASE64.decode(secretKey);
//        return Keys.hmacShaKeyFor(bytes);
        String stringKey =secretKey;
        byte[] encodedKey =Decoders.BASE64.decode(stringKey);
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length,
                "HmacSHA512");
        return key;
    }

    private Claims extractAllClaims(String token){
        return Jwts.parser()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token).getPayload();
    }
    public <T> T extractClaim(String token, Function<Claims,T> claimsResolver){
        final Claims claims=this.extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isTokenExpired(String token){



        Date expirationDate= extractAllClaims(token).getExpiration();
        return expirationDate.before(new Date());
    }

    public String extractPhoneNumber(String token){
        Claims claims = extractAllClaims(token);
        return claims.get("phoneNumber", String.class);
    }
    public String getEmailFromToken(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("email", String.class);
    }
    public boolean validateToken(String token, UserDetails userDetails){
        String phoneNumber= extractPhoneNumber(token);
        return (phoneNumber.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }
}
