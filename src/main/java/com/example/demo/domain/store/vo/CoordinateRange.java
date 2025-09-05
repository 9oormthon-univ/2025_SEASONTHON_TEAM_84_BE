package com.example.demo.domain.store.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoordinateRange {

    private Double minLatitude;
    private Double maxLatitude;
    private Double minLongitude;
    private Double maxLongitude;

    public boolean contains(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            return false;
        }
        
        return latitude >= minLatitude && latitude <= maxLatitude &&
               longitude >= minLongitude && longitude <= maxLongitude;
    }

    public boolean isValid() {
        return minLatitude != null && maxLatitude != null &&
               minLongitude != null && maxLongitude != null &&
               minLatitude <= maxLatitude &&
               minLongitude <= maxLongitude &&
               minLatitude >= -90.0 && maxLatitude <= 90.0 &&
               minLongitude >= -180.0 && maxLongitude <= 180.0;
    }

    public Coordinate getCenter() {
        if (!isValid()) {
            return null;
        }
        
        double centerLat = (minLatitude + maxLatitude) / 2.0;
        double centerLon = (minLongitude + maxLongitude) / 2.0;
        
        return new Coordinate(centerLat, centerLon);
    }

    public double getDiagonalDistanceKm() {
        if (!isValid()) {
            return 0.0;
        }
        
        return calculateDistance(minLatitude, minLongitude, maxLatitude, maxLongitude);
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
}
