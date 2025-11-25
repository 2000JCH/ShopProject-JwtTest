package com.apple.shop.member;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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

        //쿠키가 있을때만 로그를 찍어줘 (즉 로그인 성공시에만 출력)
        System.out.println("[JwtFilter] 요청 URI: " + request.getRequestURI() + ", 메서드: " + request.getMethod());

        // 1. 이름이 jwt인 쿠키 찾기
        String jwtCookie = "";
        for (int i = 0; i < cookies.length; i++) {
            if (cookies[i].getName().equals("jwt")) {
                jwtCookie = cookies[i].getValue();
            }
        }

        // 2. 유효기간, 위조여부 등 확인해보기
        Claims claim;  // 유저의 정보가 들어있음
        try{
            claim = JwtUtil.extractToken(jwtCookie); // jwt 까보기(유효한지 확인)
        }catch (Exception e){
            filterChain.doFilter(request, response);
            return;
        }

        // 3. 유효하면 Auth 변수에 유저정보 추가해주기
        // customUser에 권한을 넣으려면 SimpleGrantedAuthority에 담아서 넣어줘야함
        String[] arr = claim.get("authorities").toString().split(","); //권한을 리스트로 만들기
        List<SimpleGrantedAuthority> authorities = Arrays.stream(arr).map(a -> new SimpleGrantedAuthority(a)).toList();

        CustomUser customUser = new CustomUser( //CustomUser 클래스를 참조 -> username, password, authorities만 참조
                claim.get("username").toString(),
                "none",
                authorities
        ); //username, password, authority 필수적으로 넣어야함.
        //jwt 토큰을 만들때 displayName, id값을 포함하게 함,
        // 주문하기 기능을 위해 id값도 참조하게 해야함
        customUser.displayName = claim.get("displayName").toString();
        customUser.id = ((Number) claim.get("id")).longValue();


        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                customUser,
                null,
                authorities
                //claim.get("username").toString(), ""    //유저네임(사용자에 대한 상세정보를 담은 객체일 수도 있음), 패스워드은 필수, 여기있는게 auth.getPrincipal()에 추가됨
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
