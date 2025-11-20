package com.apple.shop;

import com.apple.shop.member.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setHeaderName("X-XSRF-TOKEN");
        return repository;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf((csrf) -> csrf.disable());

        // 세션 데이터를 생성하지마세요.
        http.sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.authorizeHttpRequests((authorize) -> authorize
                .requestMatchers("/**").permitAll()
        );

        http.addFilterBefore(new JwtFilter(), ExceptionTranslationFilter.class);

//        //나는 폼으로 로그인 하겠다.
//            http.formLogin((formLogin)
//                -> formLogin.loginPage("/login")    //로그인페이지 URL
//                .defaultSuccessUrl("/index.html") // 로그인 성공시 이동할 url //static 폴더는 정적이라 뒤에 .html을 붙여줘야 한다.
//                //.failureUrl("/fail")    //실패시 이동할 url 안적으면 /login?error 페이지로 이동함
//                // -> formLogin.loginPage("/login") 여기서 /login이라고 해놨기 때문에 /login?error 로 이동
//        );
        http.logout(logout -> logout.logoutUrl("/logout")); // url에서 /logout으로 get 요청하면 로그아웃 됨

        return http.build();
    }
}