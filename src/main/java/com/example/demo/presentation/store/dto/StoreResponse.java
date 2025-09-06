package com.example.demo.presentation.store.dto;

import com.example.demo.domain.store.entity.Category;
import com.example.demo.domain.store.entity.Store;
import com.example.demo.domain.store.entity.StoreMenu;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 업소 응답 DTO
 */
public class StoreResponse {

    /**
     * 업소 기본 정보 응답 DTO
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "업소 기본 정보 응답")
    public static class StoreInfo {

        @Schema(description = "업소 ID", example = "1")
        private Long storeId;

        @Schema(description = "업소명", example = "착한식당")
        private String storeName;

        @Schema(description = "업종", example = "RESTAURANT")
        private Category businessType;

        @Schema(description = "업종 설명", example = "음식점")
        private String businessTypeDescription;

        @Schema(description = "대분류", example = "한식")
        private String majorCategory;

        @Schema(description = "소분류", example = "육류")
        private String subCategory;

        @Schema(description = "연락처", example = "02-1234-5678")
        private String contactNumber;

        @Schema(description = "주소 정보")
        private AddressInfo address;

        @Schema(description = "활성화 여부", example = "true")
        private boolean isActive;

        @Schema(description = "생성일시", example = "2024-01-01T00:00:00")
        private LocalDateTime createdDate;

        @Schema(description = "수정일시", example = "2024-01-01T00:00:00")
        private LocalDateTime lastModifiedDate;

        public static StoreInfo from(Store store) {
            return StoreInfo.builder()
                    .storeId(store.getId())
                    .storeName(store.getStoreName())
                    .businessType(store.getCategory())
                    .businessTypeDescription(store.getCategory().getDescription())
                    .contactNumber(store.getContactNumber())
                    .majorCategory(store.getMajorCategory())
                    .subCategory(store.getSubCategory())
                    .address(AddressInfo.from(store.getAddress()))
                    .isActive(store.isActive())
                    .createdDate(store.getCreatedDate())
                    .lastModifiedDate(store.getLastModifiedDate())
                    .build();
        }
    }

    /**
     * 업소 상세 정보 응답 DTO (메뉴 포함)
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "업소 상세 정보 응답")
    public static class StoreDetail {

        //todo implement review
        @Schema(description = "업소 ID", example = "1")
        private Long storeId;

        @Schema(description = "업소명", example = "착한식당")
        private String storeName;

        @Schema(description = "업종", example = "RESTAURANT")
        private Category category;

        @Schema(description = "업종 설명", example = "음식점")
        private String categoryDescription;

        @Schema(description = "대분류", example = "한식")
        private String majorCategory;

        @Schema(description = "소분류", example = "육류")
        private String subCategory;

        @Schema(description = "연락처", example = "02-1234-5678")
        private String contactNumber;

        @Schema(description = "주소 정보")
        private AddressInfo address;

        @Schema(description = "메뉴 목록")
        private List<MenuInfo> menus;

        @Schema(description = "활성화 여부", example = "true")
        private boolean isActive;

        @Schema(description = "생성일시", example = "2024-01-01T00:00:00")
        private LocalDateTime createdDate;

        @Schema(description = "수정일시", example = "2024-01-01T00:00:00")
        private LocalDateTime lastModifiedDate;

        public static StoreDetail from(Store store) {
            return StoreDetail.builder()
                    .storeId(store.getId())
                    .storeName(store.getStoreName())
                    .category(store.getCategory())
                    .categoryDescription(store.getCategory().getDescription())
                    .contactNumber(store.getContactNumber())
                    .address(AddressInfo.from(store.getAddress()))
                    .majorCategory(store.getMajorCategory())
                    .subCategory(store.getSubCategory())
                    .menus(store.getMenus().stream()
                            .map(MenuInfo::from)
                            .toList())
                    .isActive(store.isActive())
                    .createdDate(store.getCreatedDate())
                    .lastModifiedDate(store.getLastModifiedDate())
                    .build();
        }
    }

    /**
     * 지도용 간단한 업소 정보 DTO
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "지도용 업소 정보")
    public static class MapStoreInfo {

        @Schema(description = "업소 ID", example = "1")
        private Long storeId;

        @Schema(description = "업소명", example = "착한식당")
        private String storeName;

        @Schema(description = "업종", example = "RESTAURANT")
        private Category category;

        @Schema(description = "위도", example = "37.5665")
        private Double latitude;

        @Schema(description = "경도", example = "126.9780")
        private Double longitude;

        @Schema(description = "주소", example = "서울특별시 중구 세종대로 110")
        private String address;

        public static MapStoreInfo from(Store store) {
            return MapStoreInfo.builder()
                    .storeId(store.getId())
                    .storeName(store.getStoreName())
                    .category(store.getCategory())
                    .latitude(store.getAddress().getLatitude())
                    .longitude(store.getAddress().getLongitude())
                    .address(store.getAddress().getFullAddress())
                    .build();
        }
    }

    /**
     * 주소 정보 DTO
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "주소 정보")
    public static class AddressInfo {

        @Schema(description = "시도", example = "서울특별시")
        private String sido;

        @Schema(description = "시군", example = "중구")
        private String sigun;

        @Schema(description = "전체 주소", example = "서울특별시 중구 세종대로 110")
        private String fullAddress;

        @Schema(description = "위도", example = "37.5665")
        private Double latitude;

        @Schema(description = "경도", example = "126.9780")
        private Double longitude;

        public static AddressInfo from(com.example.demo.domain.store.entity.Address address) {
            if (address == null) {
                return null;
            }

            return AddressInfo.builder()
                    .sido(address.getSido())
                    .sigun(address.getSigun())
                    .fullAddress(address.getFullAddress())
                    .latitude(address.getLatitude())
                    .longitude(address.getLongitude())
                    .build();
        }
    }

    /**
     * 메뉴 정보 DTO
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "메뉴 정보")
    public static class MenuInfo {

        @Schema(description = "메뉴 ID", example = "1")
        private Long menuId;

        @Schema(description = "메뉴명", example = "김치찌개")
        private String menuName;

        @Schema(description = "가격", example = "8000")
        private BigDecimal price;

        @Schema(description = "메뉴 순서", example = "1")
        private Integer menuOrder;

        public static MenuInfo from(StoreMenu menu) {
            return MenuInfo.builder()
                    .menuId(menu.getId())
                    .menuName(menu.getMenuName())
                    .price(menu.getPrice())
                    .menuOrder(menu.getMenuOrder())
                    .build();
        }
    }

    /**
     * 거리 정보가 포함된 업소 응답 DTO
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "거리 정보가 포함된 업소 응답")
    public static class NearbyStore {

        @Schema(description = "업소 ID", example = "1")
        private Long storeId;

        @Schema(description = "업소명", example = "착한식당")
        private String storeName;

        @Schema(description = "업종", example = "Category")
        private Category category;

        @Schema(description = "업종 설명", example = "음식점")
        private String categoryDescription;

        @Schema(description = "연락처", example = "02-1234-5678")
        private String contactNumber;

        @Schema(description = "주소 정보")
        private AddressInfo address;

        @Schema(description = "메뉴 목록")
        private List<MenuInfo> menus;

        @Schema(description = "사용자로부터의 거리(km)", example = "1.25")
        private Double distanceKm;

        @Schema(description = "활성화 여부", example = "true")
        private boolean isActive;

        @Schema(description = "생성일시", example = "2024-01-01T00:00:00")
        private LocalDateTime createdDate;

        @Schema(description = "수정일시", example = "2024-01-01T00:00:00")
        private LocalDateTime lastModifiedDate;

        public static NearbyStore from(Store store, Double distanceKm) {
            return NearbyStore.builder()
                    .storeId(store.getId())
                    .storeName(store.getStoreName())
                    .category(store.getCategory())
                    .categoryDescription(store.getCategory().getDescription())
                    .contactNumber(store.getContactNumber())
                    .address(AddressInfo.from(store.getAddress()))
                    .menus(store.getMenus().stream()
                            .map(MenuInfo::from)
                            .toList())
                    .distanceKm(Math.round(distanceKm * 100.0) / 100.0) // 소수점 2자리까지 반올림
                    .isActive(store.isActive())
                    .createdDate(store.getCreatedDate())
                    .lastModifiedDate(store.getLastModifiedDate())
                    .build();
        }
    }

    /**
     * 주변 업소 목록 응답 DTO
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "주변 업소 목록 응답")
    public static class NearbyStoreList {

        @Schema(description = "사용자 위치 정보")
        private Double userLatitude;

        @Schema(description = "사용자 위치 정보")
        private Double userLongitude;

        @Schema(description = "주변 업소 목록")
        private List<NearbyStore> stores;

        @Schema(description = "조회된 업소 개수", example = "10")
        private Integer totalCount;

        @Schema(description = "최대 거리(km)", example = "5.67")
        private Double maxDistanceKm;

        public static NearbyStoreList from(Double userLatitude, Double userLongitude,
                                           List<NearbyStore> nearbyStores) {
            Double maxDistance = nearbyStores.stream()
                    .mapToDouble(NearbyStore::getDistanceKm)
                    .max()
                    .orElse(0.0);

            return NearbyStoreList.builder()
                    .userLatitude(userLatitude)
                    .userLongitude(userLongitude)
                    .stores(nearbyStores)
                    .maxDistanceKm(Math.round(maxDistance * 100.0) / 100.0)
                    .build();
        }
    }


}
