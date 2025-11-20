package com.apple.shop.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


//<Item 테이블 (어떤 테이블과 연결된 클래스인지),  기본키의 타입>
// 테이블을 직접 다루는 게 아니라, 엔티티 클래스를 통해 테이블과 연결하기 때문에 Item 클래스가 반드시 있어야 한다.
public interface ItemRepository extends JpaRepository<Item, Long> {
    Slice<Item> findPageBy(Pageable pageable);
    List<Item> findAllByTitleContains(String title);

    @Query(value = "select * from item where match(title) against(?1)", nativeQuery = true)
    List<Item> rawQuery1(String title);
}