package com.example.demo.domain.store.vo;

import com.example.demo.domain.store.entity.BusinessType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 업소 검색 조건을 나타내는 Value Object
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreSearchCondition {

    private String storeName;        // 업소명
    private BusinessType businessType; // 업종
    private String sido;             // 시도
    private String sigun;            // 시군
    private Double latitude;         // 중심 위도
    private Double longitude;        // 중심 경도
    private Double radiusKm;         // 반경(km)
    private Boolean isActive;        // 활성화 여부

    /**
     * 지역 검색 조건이 있는지 확인
     */
    public boolean hasRegionCondition() {
        return (sido != null && !sido.trim().isEmpty()) || 
               (sigun != null && !sigun.trim().isEmpty());
    }

    /**
     * 좌표 기반 검색 조건이 있는지 확인
     */
    public boolean hasLocationCondition() {
        return latitude != null && longitude != null && radiusKm != null;
    }

    /**
     * 업소명 검색 조건이 있는지 확인
     */
    public boolean hasStoreNameCondition() {
        return storeName != null && !storeName.trim().isEmpty();
    }

    /**
     * 업종 검색 조건이 있는지 확인
     */
    public boolean hasBusinessTypeCondition() {
        return businessType != null;
    }

    /**
     * 검색 조건이 전혀 없는지 확인
     */
    public boolean isEmpty() {
        return !hasStoreNameCondition() && 
               !hasBusinessTypeCondition() && 
               !hasRegionCondition() && 
               !hasLocationCondition();
    }

    /**
     * 복합 검색 조건인지 확인 (2개 이상의 조건)
     */
    public boolean isComplexSearch() {
        int conditionCount = 0;
        if (hasStoreNameCondition()) conditionCount++;
        if (hasBusinessTypeCondition()) conditionCount++;
        if (hasRegionCondition()) conditionCount++;
        if (hasLocationCondition()) conditionCount++;
        
        return conditionCount >= 2;
    }
}
