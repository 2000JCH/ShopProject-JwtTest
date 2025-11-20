package com.apple.shop.member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public void addMember(String displayName, String username, String password){
        Member member = new Member();
        member.setDisplayName(displayName);
        member.setUsername(username);
        var hash = passwordEncoder.encode(password); //해싱해주세요~ //var hash = BCryptPasswordEncoder().encode(password);
        member.setPassword(hash);
        memberRepository.save(member);
    }
}
