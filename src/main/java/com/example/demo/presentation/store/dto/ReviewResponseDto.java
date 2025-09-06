package com.example.demo.presentation.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
@Builder
public class ReviewResponseDto {
    @Schema(description = "리뷰 ID", example = "100")
    private Long id;

    @Schema(description = "리뷰 내용", example = "음식이 정말 맛있어요!")
    private String content;

    @Schema(description = "작성자 ID", example = "1")
    private Long writerId;

    @Schema(description = "별점 (1~5)", example = "5")
    private int rating;

    @Schema(description = "업소 이름", example = "김밥천국")
    private String storeName;

    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
}
