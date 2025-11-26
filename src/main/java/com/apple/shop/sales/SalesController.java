package com.apple.shop.sales;

import com.apple.shop.item.Item;
import com.apple.shop.item.ItemRepository;
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
    private final ItemRepository itemRepository;
    private final SalesService salesService;

    @PostMapping("/order")
    String postOrder(@RequestParam String title,
                     @RequestParam Integer price,
                     @RequestParam Integer count,
                     @RequestParam Long id,
                     Authentication auth) {

        return salesService.addSales(title, price, count, id, auth);
    }

    @GetMapping("/order/all")
    String getOrderAll(){
//        List<Sales> result = salesRepository.customFindAll();
//        System.out.println(result);

        Optional<Member> result = memberRepository.findById(8L);
        System.out.println(result.get().getSales());

        return "list.html";
    }
}

class SalesDto {
    public String itemName;
    public Integer price;
    public String username;
}