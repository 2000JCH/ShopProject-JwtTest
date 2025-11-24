package com.apple.shop.member;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    //회원 가입 (유저 등록)
    @GetMapping("/register")
    String register() {
        return "register.html";
    }
    @PostMapping("/member")
    String addMember(String displayName, String username, String password) {
        memberService.addMember(displayName, username, password);
        return "redirect:/list";
    }

    // 로그인
    @GetMapping("/login")
    String login() {
        return "login.html";
    }

    //로그인 한 유저만 볼 수 있게 해야함
    @GetMapping("/my-page")
    String myPage(Authentication auth) {
        CustomUser result = (CustomUser) auth.getPrincipal();
        System.out.println(result.displayName);
        return "mypage";
    }

    @GetMapping("/user/1")
    @ResponseBody
    MemberDto getUser() {
        var result = memberRepository.findById(1L).orElseThrow(() -> new RuntimeException("Member not found"));
        return new MemberDto(result.getUsername(), result.getDisplayName());
    }

    @PostMapping("/login/jwt")
    @ResponseBody
    public ResponseEntity<String> loginJWT(@RequestBody Map<String, String> data, HttpServletResponse response) {
        try{
            //로그인 시켜주세요, auth 만들기
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    data.get("username"), data.get("password")
            );
            Authentication auth = authenticationManagerBuilder.getObject().authenticate(authToken); //아이디 + 비번 -> id, password를 DB내용과 비교해서 로그인 시켜줌 (수동 로그인 해준거) // MyUserDetailsService 호출
            SecurityContextHolder.getContext().setAuthentication(auth); // 로그인됨이라고 시스템에 등록

            //JWT 만들어서 보내주기
            String jwt = JwtUtil.createToken(SecurityContextHolder.getContext().getAuthentication());
            //System.out.println(jwt);

            //쿠키에 저장해주세요~
            Cookie cookie = new Cookie("jwt", jwt);
            cookie.setMaxAge(10); //JWT 유효기간이랑 비슷하게 또는 길게 하면 됨
            cookie.setHttpOnly(true);   // 해킹범이 쿠키를 자바스크립트로 조작하기 어려워짐
            cookie.setPath("/");    //쿠키가 전송될 URL
            response.addCookie(cookie);

            System.out.println("로그인 성공 JWT: " + jwt);

            return ResponseEntity.ok(jwt);

        } catch (Exception e){
            System.out.println("로그인 실패");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패");
        }
    }
    @GetMapping("/my-page/jwt")
    @ResponseBody
    String mypageJWT(Authentication auth) {

        CustomUser user = (CustomUser) auth.getPrincipal();
        System.out.println(user);
        System.out.println(user.displayName);
        System.out.println(user.getAuthorities());


        return "";
    }
}

