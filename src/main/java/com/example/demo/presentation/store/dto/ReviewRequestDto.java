package com.example.demo.presentation.store.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewRequestDto {
    @Schema(description = "리뷰 내용", example = "음식이 정말 맛있어요!")
    private String content;

    @Schema(description = "작성자 ID", example = "1")
    private Long writerId;

    @Schema(description = "업소 ID", example = "10")
    private Long storeId;

    @Schema(description = "별점 (1~5)", example = "5")
    private int rating;
}
