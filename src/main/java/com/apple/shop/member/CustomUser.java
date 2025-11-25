package com.apple.shop.member;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CustomUser extends User {
    public String displayName;  // 다음에 만들때 private 으로 만들어서 getter로 꺼내쓰는게 좋음
    public Long id;
    public CustomUser(String username,
                      String password,
                      Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

//    public String getDisplayName() {
//        return displayName;
//    }
//
//    public Long getId() {
//        return id;
//    }
}
