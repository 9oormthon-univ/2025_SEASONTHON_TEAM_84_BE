package com.example.demo.domain.store.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 주소 정보를 나타내는 임베디드 객체
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Address {

    @Column(name = "sido", length = 50)
    private String sido; // 시도

    @Column(name = "sigun", length = 50)
    private String sigun; // 시군

    @Column(name = "full_address", length = 500)
    private String fullAddress; // 전체 주소

    @Column(name = "latitude")
    private Double latitude; // 위도

    @Column(name = "longitude")
    private Double longitude; // 경도

    /**
     * 좌표 정보 업데이트
     */
    public Address updateCoordinates(Double latitude, Double longitude) {
        return new Address(this.sido, this.sigun, this.fullAddress, latitude, longitude);
    }

    /**
     * 주소 정보가 유효한지 검증
     */
    public boolean isValidAddress() {
        return sido != null && !sido.trim().isEmpty() 
               && sigun != null && !sigun.trim().isEmpty()
               && fullAddress != null && !fullAddress.trim().isEmpty();
    }

    /**
     * 좌표 정보가 유효한지 검증
     */
    public boolean hasValidCoordinates() {
        return latitude != null && longitude != null
               && latitude >= -90.0 && latitude <= 90.0
               && longitude >= -180.0 && longitude <= 180.0;
    }
}
