package com.example.demo.presentation.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRankingDto {
    @Schema(description = "순위", example = "1")
    private int rank;

    @Schema(description = "업소명", example = "착한식당")
    private String storeName;

    @Schema(description = "평균 별점", example = "4.5")
    private double averageRating;

}

