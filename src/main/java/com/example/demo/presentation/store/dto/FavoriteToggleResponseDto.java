package com.example.demo.presentation.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteToggleResponseDto {

    private Long storeId;
    private boolean isFavorite;
    private String message;
}
