package com.example.demo.domain.store.entity;

import com.example.demo.domain.auditing.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * 업소 메뉴 정보를 나타내는 엔티티
 */
@Getter
@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "store_menu",
    indexes = {
        @Index(name = "idx_store_menu_store_id", columnList = "store_id"),
        @Index(name = "idx_store_menu_name", columnList = "menuName")
    }
)
public class StoreMenu extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "menu_name", nullable = false, length = 100)
    private String menuName; // 메뉴명

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price; // 가격

    @Column(name = "menu_order")
    private Integer menuOrder; // 메뉴 순서 (1, 2, 3, 4...)

    /**
     * 가격 업데이트
     */
    public StoreMenu updatePrice(BigDecimal newPrice) {
        if (newPrice != null && newPrice.compareTo(BigDecimal.ZERO) > 0) {
            return StoreMenu.builder()
                .id(this.id)
                .store(this.store)
                .menuName(this.menuName)
                .price(newPrice)
                .menuOrder(this.menuOrder)
                .build();
        }
        return this;
    }

    /**
     * 메뉴명 업데이트
     */
    public StoreMenu updateMenuName(String newMenuName) {
        if (newMenuName != null && !newMenuName.trim().isEmpty()) {
            return StoreMenu.builder()
                .id(this.id)
                .store(this.store)
                .menuName(newMenuName.trim())
                .price(this.price)
                .menuOrder(this.menuOrder)
                .build();
        }
        return this;
    }

    /**
     * 가격이 유효한지 검증
     */
    public boolean hasValidPrice() {
        return price != null && price.compareTo(BigDecimal.ZERO) > 0;
    }
}
