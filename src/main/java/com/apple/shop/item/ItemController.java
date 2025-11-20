package com.apple.shop.item;

import com.apple.shop.comment.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemRepository itemRepository;
    private final ItemService itemService;
    private final S3Service s3Service;
    private final CommentRepository commentRepository;

    @GetMapping("/list")
    String list(Model model) {
        model.addAttribute("items",itemService.result());
        return "list.html";
    }

    //상품 추가
    @GetMapping("/write")
    String write() {
        return "write.html";
    }
    @PostMapping("/add")
    String addPost(String title, Integer price, String imageUrl, Authentication auth) {
        itemService.saveItem(title, price, imageUrl, auth);
        return "redirect:/list";
    }

    //상세 페이지
    @GetMapping("/detail/{id}")
    String detail(@PathVariable Long id, Model model) {
        var res = commentRepository.findAllByParentId(id);
        //System.out.println("댓글 개수: " + res.size());

        Optional<Item> result = itemRepository.findById(id);
        if (result.isPresent()) {   //result 변수에 뭔가가 들어있으면 실행
            model.addAttribute("comments", res);
            model.addAttribute("data", result.get());
            return "detail.html";
        }else  {
            return "redirect:/list";
        }
    }

    //수정기능
    @GetMapping("/edit/{id}")
    String edit(@PathVariable Long id,Model model) {
        Optional<Item> result = itemRepository.findById(id);
        if (result.isPresent()) {
            model.addAttribute("data", result.get());
            return "edit.html";
        }else {
            return "redirect:/list";
        }
    }
    @PostMapping("/edit")
    String editItem(Long id, String title, Integer price) {
        itemService.updateItem(id, title, price);
        return "redirect:/list";
    }

    //삭제기능
    @DeleteMapping("/delete")
    ResponseEntity<String> deleteItem(@RequestParam Long id) {
        itemRepository.deleteById(id);
        return ResponseEntity.status(200).body("삭제완료");
    }

//    @GetMapping("/list/page/{id}")
//    String getListPage(@PathVariable Integer id, Model model) {
//        Page<Item> result = itemRepository.findPageBy(PageRequest.of(id-1,5));
//        var totalPage = result.getTotalPages();
//        model.addAttribute("items", result);
//        model.addAttribute("totalPage", totalPage);
//
//        return "list.html";
//    }

    //이미지 업로드
    @GetMapping("/presigned-url")
    @ResponseBody
    String getURL(@RequestParam String filename) {
        var resutl = s3Service.createPresignedUrl("test/"+filename ); //https://mybucket.s3.amazonaws.com/test/workspace-1280538_1280.jpg?X-Amz-Algorithm=... 이런느낌
        System.out.println(resutl);
        return resutl;
    }

    // 검색결과
    @PostMapping("/search")
    String postSearch(@RequestParam String searchText, Model model) {
        var result = itemRepository.rawQuery1(searchText);
        // List<Item> items = itemRepository.rawQuery1(searchText);
        System.out.println(result);
        model.addAttribute("search", result);
        return "searchResults";
    }
}