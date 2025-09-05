package com.example.demo.domain.store.vo;

import com.example.demo.domain.store.entity.BusinessType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreSearchCondition {

    private String storeName;
    private BusinessType businessType;
    private String sido;
    private String sigun;
    private Double latitude;
    private Double longitude;
    private Double radiusKm;
    private Boolean isActive;

    public boolean hasRegionCondition() {
        return (sido != null && !sido.trim().isEmpty()) || 
               (sigun != null && !sigun.trim().isEmpty());
    }

    public boolean hasLocationCondition() {
        return latitude != null && longitude != null && radiusKm != null;
    }

    public boolean hasStoreNameCondition() {
        return storeName != null && !storeName.trim().isEmpty();
    }

    public boolean hasBusinessTypeCondition() {
        return businessType != null;
    }

    public boolean isEmpty() {
        return !hasStoreNameCondition() && 
               !hasBusinessTypeCondition() && 
               !hasRegionCondition() && 
               !hasLocationCondition();
    }

    public boolean isComplexSearch() {
        int conditionCount = 0;
        if (hasStoreNameCondition()) conditionCount++;
        if (hasBusinessTypeCondition()) conditionCount++;
        if (hasRegionCondition()) conditionCount++;
        if (hasLocationCondition()) conditionCount++;
        
        return conditionCount >= 2;
    }
}
