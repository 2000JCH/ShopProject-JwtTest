package com.apple.shop.member;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    @Value("${jwt.secretKey}")
    private String secretKey;

    private static SecretKey key;

    @PostConstruct
    public void init(){
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

//    static final SecretKey key =
//            Keys.hmacShaKeyFor(Decoders.BASE64.decode(
//                    "jwtpassword123jwtpassword123jwtpassword123jwtpassword123jwtpassword" //application.properties에 저장한 후 끌어와서 쓰는게 더 좋다.
//            ));

    // JWT 만들어주는 함수
    public static String createToken(Authentication auth) {
        CustomUser user = (CustomUser) auth.getPrincipal();
        String authorities = auth.getAuthorities().stream().map(a -> a.getAuthority())
                .collect(Collectors.joining(","));

        String jwt = Jwts.builder()
                .claim("username", user.getUsername())
                .claim("displayName", user.displayName)
                .claim("authorities", authorities)
                .issuedAt(new Date(System.currentTimeMillis())) //발행 시기
                .expiration(new Date(System.currentTimeMillis() + 10000)) //유효기간 10초
                .signWith(key)
                .compact();
        return jwt;
    }

    // JWT 까주는 함수
    public static Claims extractToken(String token) {
        Claims claims = Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload();
        return claims;
    }
}