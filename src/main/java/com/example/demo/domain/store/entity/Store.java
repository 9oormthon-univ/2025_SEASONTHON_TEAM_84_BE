package com.example.demo.domain.store.entity;

import com.example.demo.domain.auditing.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "store",
    indexes = {
        @Index(name = "idx_store_name", columnList = "storeName"),
        @Index(name = "idx_store_category", columnList = "category"),
        @Index(name = "idx_store_sido_sigun", columnList = "sido, sigun"),
        @Index(name = "idx_store_coordinates", columnList = "latitude, longitude")
    }
)
public class Store extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Long id;

    @Column(name = "store_name", nullable = false, length = 200)
    private String storeName;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private Category category;

    // 대분류/소분류 카테고리 (UI/검색용)
    @Column(name = "major_category", nullable = false, length = 50)
    private String majorCategory;

    @Column(name = "sub_category", nullable = false, length = 50)
    private String subCategory;

    @Column(name = "contact_number", length = 20)
    private String contactNumber;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<StoreMenu> menus = new ArrayList<>();

    @Column(name = "is_active")
    @Builder.Default
    private boolean isActive = true;

    public void addMenu(String menuName, BigDecimal price) {
        if (menuName != null && !menuName.trim().isEmpty() && price != null && price.compareTo(BigDecimal.ZERO) > 0) {
            StoreMenu menu = StoreMenu.builder()
                .store(this)
                .menuName(menuName.trim())
                .price(price)
                .build();
            this.menus.add(menu);
        }
    }

    public void updateCoordinates(Double latitude, Double longitude) {
        if (this.address != null) {
            this.address = this.address.updateCoordinates(latitude, longitude);
        }
    }

    public Store updateStoreInfo(String storeName, Category businessType, String contactNumber) {
        return Store.builder()
            .id(this.id)
            .storeName(storeName != null ? storeName : this.storeName)
            .category(businessType != null ? businessType : this.category)
            .contactNumber(contactNumber != null ? contactNumber : this.contactNumber)
            .majorCategory(this.majorCategory)
            .subCategory(this.subCategory)
            .address(this.address)
            .menus(this.menus)
            .isActive(this.isActive)
            .build();
    }

    public void toggleActiveStatus() {
        this.isActive = !this.isActive;
    }

    public boolean isInRegion(String sido, String sigun) {
        if (this.address == null) return false;
        
        boolean sidoMatch = sido == null || sido.trim().isEmpty() || 
                           (this.address.getSido() != null && this.address.getSido().contains(sido));
        boolean sigunMatch = sigun == null || sigun.trim().isEmpty() || 
                            (this.address.getSigun() != null && this.address.getSigun().contains(sigun));
        
        return sidoMatch && sigunMatch;
    }

    public boolean isWithinRadius(Double centerLat, Double centerLon, Double radiusKm) {
        if (this.address == null || !this.address.hasValidCoordinates() || 
            centerLat == null || centerLon == null || radiusKm == null) {
            return false;
        }

        double distance = calculateDistance(
            this.address.getLatitude(), this.address.getLongitude(),
            centerLat, centerLon
        );
        
        return distance <= radiusKm;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }

    public static Store create(String storeName,
                               Category businessType,
                               String contactNumber,
                               Address address) {
        Category.Classification classification = Category.classify(
            businessType != null ? businessType.getDescription() : null
        );
        return Store.builder()
            .storeName(storeName != null ? storeName.trim() : null)
            .category(businessType)
            .contactNumber(contactNumber != null ? contactNumber.trim() : null)
            .address(address)
            .majorCategory(classification.majorCategory())
            .subCategory(classification.subCategory())
            .isActive(true)
            .build();
    }

    public static Store create(String storeName,
                               Category businessType,
                               String contactNumber,
                               Address address,
                               String majorCategory,
                               String subCategory) {
        String major = majorCategory != null && !majorCategory.trim().isEmpty()
            ? majorCategory.trim()
            : Category.classify(businessType != null ? businessType.getDescription() : null).majorCategory();
        String sub = subCategory != null && !subCategory.trim().isEmpty()
            ? subCategory.trim()
            : Category.classify(businessType != null ? businessType.getDescription() : null).subCategory();

        return Store.builder()
            .storeName(storeName != null ? storeName.trim() : null)
            .category(businessType)
            .contactNumber(contactNumber != null ? contactNumber.trim() : null)
            .address(address)
            .majorCategory(major)
            .subCategory(sub)
            .isActive(true)
            .build();
    }

    public void updateCategories(String majorCategory, String subCategory) {
        this.majorCategory = majorCategory != null ? majorCategory.trim() : this.majorCategory;
        this.subCategory = subCategory != null ? subCategory.trim() : this.subCategory;
    }
}
