package com.apple.shop.sales;

import com.apple.shop.member.CustomUser;
import com.apple.shop.member.Member;
import com.apple.shop.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class SalesController {
    private final SalesRepository salesRepository;
    private final MemberRepository memberRepository;

    @PostMapping("/order")
    String postOrder(@RequestParam String title, @RequestParam Integer price, @RequestParam Integer count, Authentication auth) {
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return "redirect:/login";
        }

        Sales sales = new Sales();
        sales.setItemName(title);
        sales.setPrice(price);
        sales.setCount(count);
        CustomUser user = (CustomUser) auth.getPrincipal(); //어떤 유저가 만들었는지 확인하기 위해
        Member member = new Member();
        member.setId(user.id);
        sales.setMember(member);
        salesRepository.save(sales);

        return "list";
    }

    @GetMapping("/order/all")
    String getOrderAll(){
//        List<Sales> result = salesRepository.customFindAll();
//        System.out.println(result);

        Optional<Member> result = memberRepository.findById(1L);
        System.out.println(result.get().getSales());


        return "list.html";
    }
}

class SalesDto {
    public String itemName;
    public Integer price;
    public String username;
}