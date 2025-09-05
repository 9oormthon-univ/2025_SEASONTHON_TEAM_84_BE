package com.example.demo.domain.store.entity;

import com.example.demo.domain.auditing.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 착한가격업소 정보를 나타내는 엔티티
 */
@Getter
@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "store",
    indexes = {
        @Index(name = "idx_store_name", columnList = "storeName"),
        @Index(name = "idx_store_business_type", columnList = "businessType"),
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
    private String storeName; // 업소명

    @Enumerated(EnumType.STRING)
    @Column(name = "business_type", nullable = false)
    private BusinessType businessType; // 업종

    @Column(name = "contact_number", length = 20)
    private String contactNumber; // 연락처

    @Embedded
    private Address address; // 주소 정보 (시도, 시군, 전체주소, 좌표)

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<StoreMenu> menus = new ArrayList<>(); // 메뉴 정보

    @Column(name = "is_active")
    @Builder.Default
    private boolean isActive = true; // 운영 여부

    // 편의 메서드들
    
    /**
     * 메뉴 추가
     */
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

    /**
     * 좌표 정보 업데이트
     */
    public void updateCoordinates(Double latitude, Double longitude) {
        if (this.address != null) {
            this.address = this.address.updateCoordinates(latitude, longitude);
        }
    }

    /**
     * 업소 정보 업데이트
     */
    public Store updateStoreInfo(String storeName, BusinessType businessType, String contactNumber) {
        return Store.builder()
            .id(this.id)
            .storeName(storeName != null ? storeName : this.storeName)
            .businessType(businessType != null ? businessType : this.businessType)
            .contactNumber(contactNumber != null ? contactNumber : this.contactNumber)
            .address(this.address)
            .menus(this.menus)
            .isActive(this.isActive)
            .build();
    }

    /**
     * 업소 활성화/비활성화
     */
    public void toggleActiveStatus() {
        this.isActive = !this.isActive;
    }

    /**
     * 업소가 특정 지역에 속하는지 확인
     */
    public boolean isInRegion(String sido, String sigun) {
        if (this.address == null) return false;
        
        boolean sidoMatch = sido == null || sido.trim().isEmpty() || 
                           (this.address.getSido() != null && this.address.getSido().contains(sido));
        boolean sigunMatch = sigun == null || sigun.trim().isEmpty() || 
                            (this.address.getSigun() != null && this.address.getSigun().contains(sigun));
        
        return sidoMatch && sigunMatch;
    }

    /**
     * 좌표 기반 거리 계산을 위한 헬퍼 메서드
     */
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

    /**
     * 하버사인 공식을 이용한 거리 계산 (km 단위)
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // 지구 반지름 (km)
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
}
