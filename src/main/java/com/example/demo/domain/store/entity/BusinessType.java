package com.example.demo.domain.store.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 업종 분류를 나타내는 열거형
 */
@Getter
@RequiredArgsConstructor
public enum BusinessType {
    RESTAURANT("음식점"),
    CAFE("카페"),
    CONVENIENCE_STORE("편의점"),
    SUPERMARKET("마트"),
    BAKERY("제과점"),
    PHARMACY("약국"),
    HOSPITAL("병원"),
    BEAUTY("미용실"),
    LAUNDRY("세탁소"),
    GAS_STATION("주유소"),
    ETC("기타");

    private final String description;

    /**
     * 문자열로부터 BusinessType을 찾아 반환
     */
    public static BusinessType fromString(String businessType) {
        if (businessType == null || businessType.trim().isEmpty()) {
            return ETC;
        }

        for (BusinessType type : BusinessType.values()) {
            if (type.description.equals(businessType.trim()) || 
                type.name().equalsIgnoreCase(businessType.trim())) {
                return type;
            }
        }
        return ETC;
    }
}
