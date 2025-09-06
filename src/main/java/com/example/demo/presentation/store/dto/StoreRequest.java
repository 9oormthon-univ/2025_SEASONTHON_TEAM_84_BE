package com.example.demo.presentation.store.dto;

import com.example.demo.domain.store.entity.Category;
import com.example.demo.domain.store.vo.StoreSearchCondition;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 업소 요청 DTO
 */
public class StoreRequest {

    /**
     * 업소 생성 요청 DTO
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "업소 생성 요청")
    public static class CreateStore {
        
        @NotBlank(message = "업소명은 필수입니다.")
        @Size(max = 200, message = "업소명은 200자 이하여야 합니다.")
        @Schema(description = "업소명", example = "착한식당", required = true)
        private String storeName;
        
        @NotNull(message = "업종은 필수입니다.")
        @Schema(description = "업종", example = "RESTAURANT", required = true)
        private Category businessType;

        @Size(max = 50, message = "대분류는 50자 이하여야 합니다.")
        @Schema(description = "대분류", example = "한식")
        private String majorCategory;

        @Size(max = 50, message = "소분류는 50자 이하여야 합니다.")
        @Schema(description = "소분류", example = "육류")
        private String subCategory;
        
        @Pattern(regexp = "^[0-9\\-\\s]+$", message = "연락처 형식이 올바르지 않습니다.")
        @Schema(description = "연락처", example = "02-1234-5678")
        private String contactNumber;
        
        @NotBlank(message = "시도는 필수입니다.")
        @Size(max = 50, message = "시도는 50자 이하여야 합니다.")
        @Schema(description = "시도", example = "서울특별시", required = true)
        private String sido;
        
        @NotBlank(message = "시군은 필수입니다.")
        @Size(max = 50, message = "시군은 50자 이하여야 합니다.")
        @Schema(description = "시군", example = "중구", required = true)
        private String sigun;
        
        @NotBlank(message = "주소는 필수입니다.")
        @Size(max = 500, message = "주소는 500자 이하여야 합니다.")
        @Schema(description = "전체 주소", example = "서울특별시 중구 세종대로 110", required = true)
        private String fullAddress;
        
        @DecimalMin(value = "-90.0", message = "위도는 -90 이상이어야 합니다.")
        @DecimalMax(value = "90.0", message = "위도는 90 이하여야 합니다.")
        @Schema(description = "위도", example = "37.5665")
        private Double latitude;
        
        @DecimalMin(value = "-180.0", message = "경도는 -180 이상이어야 합니다.")
        @DecimalMax(value = "180.0", message = "경도는 180 이하여야 합니다.")
        @Schema(description = "경도", example = "126.9780")
        private Double longitude;
    }

    /**
     * 업소 수정 요청 DTO
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "업소 수정 요청")
    public static class UpdateStore {
        
        @Size(max = 200, message = "업소명은 200자 이하여야 합니다.")
        @Schema(description = "업소명", example = "착한식당")
        private String storeName;
        
        @Schema(description = "업종", example = "RESTAURANT")
        private Category businessType;

        @Size(max = 50, message = "대분류는 50자 이하여야 합니다.")
        @Schema(description = "대분류", example = "한식")
        private String majorCategory;

        @Size(max = 50, message = "소분류는 50자 이하여야 합니다.")
        @Schema(description = "소분류", example = "육류")
        private String subCategory;
        
        @Pattern(regexp = "^[0-9\\-\\s]+$", message = "연락처 형식이 올바르지 않습니다.")
        @Schema(description = "연락처", example = "02-1234-5678")
        private String contactNumber;
    }

    /**
     * 좌표 업데이트 요청 DTO
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "좌표 업데이트 요청")
    public static class UpdateCoordinates {
        
        @NotNull(message = "위도는 필수입니다.")
        @DecimalMin(value = "-90.0", message = "위도는 -90 이상이어야 합니다.")
        @DecimalMax(value = "90.0", message = "위도는 90 이하여야 합니다.")
        @Schema(description = "위도", example = "37.5665", required = true)
        private Double latitude;
        
        @NotNull(message = "경도는 필수입니다.")
        @DecimalMin(value = "-180.0", message = "경도는 -180 이상이어야 합니다.")
        @DecimalMax(value = "180.0", message = "경도는 180 이하여야 합니다.")
        @Schema(description = "경도", example = "126.9780", required = true)
        private Double longitude;
    }

    /**
     * 메뉴 추가 요청 DTO
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "메뉴 추가 요청")
    public static class AddMenu {
        
        @NotBlank(message = "메뉴명은 필수입니다.")
        @Size(max = 100, message = "메뉴명은 100자 이하여야 합니다.")
        @Schema(description = "메뉴명", example = "김치찌개", required = true)
        private String menuName;
        
        @NotNull(message = "가격은 필수입니다.")
        @DecimalMin(value = "0.0", inclusive = false, message = "가격은 0보다 커야 합니다.")
        @Digits(integer = 8, fraction = 2, message = "가격 형식이 올바르지 않습니다.")
        @Schema(description = "가격", example = "8000", required = true)
        private BigDecimal price;
    }

    /**
     * 업소 검색 요청 DTO
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "업소 검색 요청")
    public static class SearchStore {
        
        @Size(max = 200, message = "업소명은 200자 이하여야 합니다.")
        @Schema(description = "업소명 (부분 검색)", example = "착한")
        private String storeName;
        
        @Schema(description = "업종", example = "RESTAURANT")
        private Category businessType;
        
        @Size(max = 50, message = "시도는 50자 이하여야 합니다.")
        @Schema(description = "시도", example = "서울특별시")
        private String sido;
        
        @Size(max = 50, message = "시군은 50자 이하여야 합니다.")
        @Schema(description = "시군", example = "중구")
        private String sigun;
        
        @DecimalMin(value = "-90.0", message = "위도는 -90 이상이어야 합니다.")
        @DecimalMax(value = "90.0", message = "위도는 90 이하여야 합니다.")
        @Schema(description = "중심 위도", example = "37.5665")
        private Double latitude;
        
        @DecimalMin(value = "-180.0", message = "경도는 -180 이상이어야 합니다.")
        @DecimalMax(value = "180.0", message = "경도는 180 이하여야 합니다.")
        @Schema(description = "중심 경도", example = "126.9780")
        private Double longitude;
        
        @DecimalMin(value = "0.1", message = "반경은 0.1km 이상이어야 합니다.")
        @DecimalMax(value = "100.0", message = "반경은 100km 이하여야 합니다.")
        @Schema(description = "검색 반경(km)", example = "5.0")
        private Double radiusKm;

        /**
         * SearchStore DTO를 StoreSearchCondition VO로 변환
         */
        public StoreSearchCondition toSearchCondition() {
            return StoreSearchCondition.builder()
                .storeName(this.storeName)
                .businessType(this.businessType)
                .sido(this.sido)
                .sigun(this.sigun)
                .latitude(this.latitude)
                .longitude(this.longitude)
                .radiusKm(this.radiusKm)
                .isActive(true)  // 기본적으로 활성화된 업소만 검색
                .build();
        }
    }

    /**
     * 반경 검색 요청 DTO
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "반경 검색 요청")
    public static class RadiusSearch {
        
        @NotNull(message = "위도는 필수입니다.")
        @DecimalMin(value = "-90.0", message = "위도는 -90 이상이어야 합니다.")
        @DecimalMax(value = "90.0", message = "위도는 90 이하여야 합니다.")
        @Schema(description = "중심 위도", example = "37.5665", required = true)
        private Double latitude;
        
        @NotNull(message = "경도는 필수입니다.")
        @DecimalMin(value = "-180.0", message = "경도는 -180 이상이어야 합니다.")
        @DecimalMax(value = "180.0", message = "경도는 180 이하여야 합니다.")
        @Schema(description = "중심 경도", example = "126.9780", required = true)
        private Double longitude;
        
        @NotNull(message = "반경은 필수입니다.")
        @DecimalMin(value = "0.1", message = "반경은 0.1km 이상이어야 합니다.")
        @DecimalMax(value = "100.0", message = "반경은 100km 이하여야 합니다.")
        @Schema(description = "검색 반경(km)", example = "5.0", required = true)
        private Double radiusKm;
    }
}
