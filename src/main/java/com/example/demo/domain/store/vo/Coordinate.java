package com.example.demo.domain.store.vo;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Coordinate {

    private Double latitude;
    private Double longitude;

    public boolean isValid() {
        return latitude != null && longitude != null &&
               latitude >= -90.0 && latitude <= 90.0 &&
               longitude >= -180.0 && longitude <= 180.0;
    }

    public double distanceTo(Coordinate other) {
        if (!this.isValid() || !other.isValid()) {
            return Double.MAX_VALUE;
        }
        
        return calculateDistance(this.latitude, this.longitude, 
                               other.latitude, other.longitude);
    }

    public boolean isWithinRadius(Coordinate center, double radiusKm) {
        return distanceTo(center) <= radiusKm;
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

    @Override
    public String toString() {
        return String.format("Coordinate(lat=%.6f, lon=%.6f)", latitude, longitude);
    }
}
