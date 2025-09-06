package com.example.demo.presentation.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ReviewRequest {

    @Getter
    @NoArgsConstructor
    @Schema(description = "리뷰 작성 요청")
    public static class CreateReview {
        
        @NotNull(message = "업소 ID는 필수입니다.")
        @Schema(description = "업소 ID", example = "1")
        private Long storeId;
        
        @NotNull(message = "평점은 필수입니다.")
        @Min(value = 1, message = "평점은 1점 이상이어야 합니다.")
        @Max(value = 5, message = "평점은 5점 이하여야 합니다.")
        @Schema(description = "평점 (1~5점)", example = "4")
        private Integer rating;
        
        @Size(max = 1000, message = "리뷰 내용은 1000자 이하여야 합니다.")
        @Schema(description = "리뷰 내용", example = "음식이 정말 맛있어요!")
        private String content;

        public CreateReview(Long storeId, Integer rating, String content) {
            this.storeId = storeId;
            this.rating = rating;
            this.content = content;
        }
    }

    @Getter
    @NoArgsConstructor
    @Schema(description = "리뷰 수정 요청")
    public static class UpdateReview {
        
        @NotNull(message = "평점은 필수입니다.")
        @Min(value = 1, message = "평점은 1점 이상이어야 합니다.")
        @Max(value = 5, message = "평점은 5점 이하여야 합니다.")
        @Schema(description = "평점 (1~5점)", example = "5")
        private Integer rating;
        
        @Size(max = 1000, message = "리뷰 내용은 1000자 이하여야 합니다.")
        @Schema(description = "리뷰 내용", example = "수정된 리뷰 내용입니다.")
        private String content;

        public UpdateReview(Integer rating, String content) {
            this.rating = rating;
            this.content = content;
        }
    }

    @Getter
    @NoArgsConstructor
    @Schema(description = "리뷰 목록 조회 요청")
    public static class GetReviews {
        
        @Min(value = 0, message = "페이지는 0 이상이어야 합니다.")
        @Schema(description = "페이지 번호 (0부터 시작)", example = "0")
        private Integer page = 0;
        
        @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
        @Max(value = 50, message = "페이지 크기는 50 이하여야 합니다.")
        @Schema(description = "페이지 크기", example = "10")
        private Integer size = 10;

        public GetReviews(Integer page, Integer size) {
            this.page = page != null ? page : 0;
            this.size = size != null ? size : 10;
        }
    }
}
