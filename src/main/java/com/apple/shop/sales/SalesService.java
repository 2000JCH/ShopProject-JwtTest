package com.apple.shop.sales;

import com.apple.shop.item.Item;
import com.apple.shop.item.ItemRepository;
import com.apple.shop.member.CustomUser;
import com.apple.shop.member.Member;
import com.apple.shop.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SalesService {
    private final ItemRepository itemRepository;
    private final SalesRepository salesRepository;

    @Transactional
    public String addSales(String title,
                           Integer price,
                           Integer count,
                           Long id,
                           Authentication auth) {
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return "redirect:/login";
        }
        //재고 빼기
        Optional<Item> result = itemRepository.findById(id);
        if (result.isPresent()) {
            Item item = result.get();
            if (count > item.getStockCount()){
                System.out.println("재고 보다 많은 수량을 요청함");
                return "redirect:/detail/" + id;
            }

            item.setStockCount(item.getStockCount() - count);
            itemRepository.save(item);

            Sales sales = new Sales();
            sales.setItemName(title);
            sales.setPrice(price);
            sales.setCount(count);

            CustomUser user = (CustomUser) auth.getPrincipal(); //어떤 유저가 만들었는지 확인하기 위해
            Member member = new Member();
            member.setId(user.id);
            sales.setMember(member);
            salesRepository.save(sales);
        }else {
            System.out.println("해당 아이템을 찾을 수 없습니다");
            return "redirect:/list";
        }
        return "redirect:/list";
    }
}
