package com.example.demo.domain.store.entity;

import com.example.demo.domain.auditing.entity.BaseTimeEntity;
import jakarta.persistence.*;
import com.example.demo.domain.store.util.DistanceUtils;
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
    private String storeName;

    @Enumerated(EnumType.STRING)
    @Column(name = "business_type", nullable = false)
    private BusinessType businessType;

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

        double distance = DistanceUtils.calculateDistanceKm(
            this.address.getLatitude(), this.address.getLongitude(),
            centerLat, centerLon
        );
        
        return distance <= radiusKm;
    }

    public static Store create(String storeName,
                               BusinessType businessType,
                               String contactNumber,
                               Address address) {
        return Store.builder()
            .storeName(storeName != null ? storeName.trim() : null)
            .businessType(businessType)
            .contactNumber(contactNumber != null ? contactNumber.trim() : null)
            .address(address)
            .isActive(true)
            .build();
    }
}
