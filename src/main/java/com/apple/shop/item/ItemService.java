package com.apple.shop.item;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    //list 페이지 화면
    public List<Item> result(){
        return itemRepository.findAll();
    }

    public void saveItem(String title, Integer price, String imageUrl,  Authentication auth) {
        if (!auth.isAuthenticated()){
            throw new RuntimeException("로그인이 필요합니다.");
        }

        if (price < 0 ){
            throw new RuntimeException("음수는 안됩니다.");
        }
        if (title.length() > 20){
            throw new RuntimeException("제목은 20자 이하여야 합니다.");
        }
        Item item = new Item();
        item.setTitle(title);
        item.setPrice(price);
        item.setImageUrl(imageUrl);
        itemRepository.save(item);
    }

    public void updateItem(Long id, String title, Integer price) {
        if (price < 0 ){
            throw new RuntimeException("음수는 안됩니다.");
        }
        if (title.length() > 20){
            throw new RuntimeException("제목은 20자 이하여야 합니다.");
        }
        Item item = new Item();
        item.setId(id); //Id가 1인 행이 없으면 아래 내용을 추가, 있으면 아래 내용으로 덮어쓰기
        item.setTitle(title);
        item.setPrice(price);
        itemRepository.save(item);
    }

}
