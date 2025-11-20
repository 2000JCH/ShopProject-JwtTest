package com.apple.shop.member;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

public class JwtFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain
    ) throws ServletException, IOException {

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            filterChain.doFilter(request, response);    //다음 필터 실행해줘
            return; // 리턴값이 없으면 리턴 밑에 있는 코드는 실행 X
        }

        // 1. 이름이 jwt인 쿠키 찾기
        String jwtCookie = "";
        for (int i = 0; i < cookies.length; i++) {
            if (cookies[i].getName().equals("jwt")) {
                jwtCookie = cookies[i].getValue();
            }
        }
        System.out.println(jwtCookie);

        // 2. 유효기간, 위조여부 등 확인해보기
        Claims claim;  // 유저의 정보가 들어있음
        try{
            claim = JwtUtil.extractToken(jwtCookie); // jwt 까보기(유효한지 확인)
        }catch (Exception e){
            filterChain.doFilter(request, response);
            return;
        }

        // 3. 유효하면 Auth 변수에 유저정보 추가해주기
        var customUser = new CustomUser();

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                claim.get("username").toString(), ""    //유저네임, 패스워드은 필수, 여기있는게 auth.getPrincipal()에 추가됨
                //많은 정보를 넣고 싶으면 CustomUser()을 하나 만들어서 넣는다.
        );
        authToken.setDetails(new WebAuthenticationDetailsSource()   //실제 auth 변수와 동일하게 만들고 싶을 때 사용
                .buildDetails(request)
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);


        // request -> 유저의 여러 정보가 들어있음 ip, 기기종류 등
        filterChain.doFilter(request, response);
    }
}
