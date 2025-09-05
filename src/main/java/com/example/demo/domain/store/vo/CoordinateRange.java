package com.example.demo.domain.store.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 좌표 범위를 나타내는 Value Object
 * 지도 뷰포트나 경계 영역을 표현할 때 사용
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoordinateRange {

    private Double minLatitude;   // 최소 위도
    private Double maxLatitude;   // 최대 위도
    private Double minLongitude;  // 최소 경도
    private Double maxLongitude;  // 최대 경도

    /**
     * 주어진 좌표가 이 범위 내에 있는지 확인
     */
    public boolean contains(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            return false;
        }
        
        return latitude >= minLatitude && latitude <= maxLatitude &&
               longitude >= minLongitude && longitude <= maxLongitude;
    }

    /**
     * 유효한 좌표 범위인지 검증
     */
    public boolean isValid() {
        return minLatitude != null && maxLatitude != null &&
               minLongitude != null && maxLongitude != null &&
               minLatitude <= maxLatitude &&
               minLongitude <= maxLongitude &&
               minLatitude >= -90.0 && maxLatitude <= 90.0 &&
               minLongitude >= -180.0 && maxLongitude <= 180.0;
    }

    /**
     * 범위의 중심점 계산
     */
    public Coordinate getCenter() {
        if (!isValid()) {
            return null;
        }
        
        double centerLat = (minLatitude + maxLatitude) / 2.0;
        double centerLon = (minLongitude + maxLongitude) / 2.0;
        
        return new Coordinate(centerLat, centerLon);
    }

    /**
     * 범위의 대각선 거리 계산 (km)
     */
    public double getDiagonalDistanceKm() {
        if (!isValid()) {
            return 0.0;
        }
        
        return calculateDistance(minLatitude, minLongitude, maxLatitude, maxLongitude);
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
}
