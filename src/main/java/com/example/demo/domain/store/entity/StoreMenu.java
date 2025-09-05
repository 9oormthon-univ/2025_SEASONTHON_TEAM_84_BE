package com.example.demo.domain.store.entity;

import com.example.demo.domain.auditing.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

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
    private String menuName;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "menu_order")
    private Integer menuOrder;

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

    public boolean hasValidPrice() {
        return price != null && price.compareTo(BigDecimal.ZERO) > 0;
    }
}
