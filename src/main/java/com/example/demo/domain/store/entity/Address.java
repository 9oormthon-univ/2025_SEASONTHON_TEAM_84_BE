package com.example.demo.domain.store.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Address {

    @Column(name = "sido", length = 50)
    private String sido;

    @Column(name = "sigun", length = 50)
    private String sigun;

    @Column(name = "full_address", length = 500)
    private String fullAddress;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    public Address updateCoordinates(Double latitude, Double longitude) {
        return new Address(this.sido, this.sigun, this.fullAddress, latitude, longitude);
    }

    public boolean isValidAddress() {
        return sido != null && !sido.trim().isEmpty() 
               && sigun != null && !sigun.trim().isEmpty()
               && fullAddress != null && !fullAddress.trim().isEmpty();
    }

    public boolean hasValidCoordinates() {
        return latitude != null && longitude != null
               && latitude >= -90.0 && latitude <= 90.0
               && longitude >= -180.0 && longitude <= 180.0;
    }
}
