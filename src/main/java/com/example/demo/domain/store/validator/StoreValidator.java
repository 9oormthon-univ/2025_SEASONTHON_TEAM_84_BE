package com.example.demo.domain.store.validator;

import com.example.demo.domain.store.exception.StoreErrorStatus;
import com.example.demo.domain.store.exception.StoreHandler;

import java.math.BigDecimal;

public final class StoreValidator {

    private StoreValidator() {}

    public static void validateCoordinates(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            throw new StoreHandler(StoreErrorStatus.INVALID_COORDINATES);
        }
        if (latitude < -90.0 || latitude > 90.0) {
            throw new StoreHandler(StoreErrorStatus.INVALID_LATITUDE);
        }
        if (longitude < -180.0 || longitude > 180.0) {
            throw new StoreHandler(StoreErrorStatus.INVALID_LONGITUDE);
        }
    }

    public static void validateRadius(Double radiusKm) {
        if (radiusKm == null || radiusKm <= 0) {
            throw new StoreHandler(StoreErrorStatus.INVALID_RADIUS);
        }
        if (radiusKm > 100.0) {
            throw new StoreHandler(StoreErrorStatus.RADIUS_TOO_LARGE);
        }
    }

    public static void validateMenuInfo(String menuName, BigDecimal price) {
        if (menuName == null || menuName.trim().isEmpty()) {
            throw new StoreHandler(StoreErrorStatus.INVALID_MENU_PRICE);
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new StoreHandler(StoreErrorStatus.INVALID_MENU_PRICE);
        }
    }
}


