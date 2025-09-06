package com.example.demo.presentation.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteResponseDto {

    private Long favoriteId;
    private Long storeId;
    private String storeName;
    //private BusinessType businessType;
    private String address;
}
