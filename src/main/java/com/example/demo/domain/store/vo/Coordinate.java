package com.example.demo.domain.store.vo;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 좌표를 나타내는 Value Object
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Coordinate {

    private Double latitude;   // 위도
    private Double longitude;  // 경도

    /**
     * 좌표 유효성 검증
     */
    public boolean isValid() {
        return latitude != null && longitude != null &&
               latitude >= -90.0 && latitude <= 90.0 &&
               longitude >= -180.0 && longitude <= 180.0;
    }

    /**
     * 다른 좌표와의 거리 계산 (km)
     */
    public double distanceTo(Coordinate other) {
        if (!this.isValid() || !other.isValid()) {
            return Double.MAX_VALUE;
        }
        
        return calculateDistance(this.latitude, this.longitude, 
                               other.latitude, other.longitude);
    }

    /**
     * 특정 반경 내에 있는지 확인
     */
    public boolean isWithinRadius(Coordinate center, double radiusKm) {
        return distanceTo(center) <= radiusKm;
    }

    /**
     * 하버사인 공식을 이용한 거리 계산
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

    @Override
    public String toString() {
        return String.format("Coordinate(lat=%.6f, lon=%.6f)", latitude, longitude);
    }
}
