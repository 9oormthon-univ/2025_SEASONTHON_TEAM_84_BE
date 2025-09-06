package com.example.demo.presentation.review.dto;

import com.example.demo.domain.review.entity.Review;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public class ReviewResponse {

    @Getter
    @Builder
    @Schema(description = "리뷰 정보")
    public static class ReviewInfo {
        
        @Schema(description = "리뷰 ID")
        private Long reviewId;
        
        @Schema(description = "업소 ID")
        private Long storeId;
        
        @Schema(description = "업소 이름")
        private String storeName;
        
        @Schema(description = "작성자 ID")
        private Long memberId;
        
        @Schema(description = "작성자 닉네임")
        private String memberNickname;
        
        @Schema(description = "평점")
        private Integer rating;
        
        @Schema(description = "리뷰 내용")
        private String content;
        
        @Schema(description = "작성일시")
        private LocalDateTime createdDate;
        
        @Schema(description = "수정일시")
        private LocalDateTime lastModifiedDate;

        public static ReviewInfo from(Review review) {
            return ReviewInfo.builder()
                    .reviewId(review.getId())
                    .storeId(review.getStore().getId())
                    .storeName(review.getStore().getStoreName())
                    .memberId(review.getMember().getId())
                    .memberNickname(review.getMember().getNickname())
                    .rating(review.getRating())
                    .content(review.getContent())
                    .createdDate(review.getCreatedDate())
                    .lastModifiedDate(review.getLastModifiedDate())
                    .build();
        }
    }

    @Getter
    @Builder
    @Schema(description = "리뷰 목록 응답")
    public static class ReviewList {
        
        @Schema(description = "리뷰 목록")
        private List<ReviewInfo> reviews;
        
        @Schema(description = "총 리뷰 수")
        private long totalCount;
        
        @Schema(description = "총 페이지 수")
        private int totalPages;
        
        @Schema(description = "현재 페이지")
        private int currentPage;
        
        @Schema(description = "페이지 크기")
        private int pageSize;
        
        @Schema(description = "마지막 페이지 여부")
        private boolean isLast;

        public static ReviewList from(Page<Review> reviewPage) {
            List<ReviewInfo> reviewInfos = reviewPage.getContent().stream()
                    .map(ReviewInfo::from)
                    .toList();
            
            return ReviewList.builder()
                    .reviews(reviewInfos)
                    .totalCount(reviewPage.getTotalElements())
                    .totalPages(reviewPage.getTotalPages())
                    .currentPage(reviewPage.getNumber())
                    .pageSize(reviewPage.getSize())
                    .isLast(reviewPage.isLast())
                    .build();
        }
    }

    @Getter
    @Builder
    @Schema(description = "업소 평점 정보")
    public static class StoreRating {
        
        @Schema(description = "업소 ID")
        private Long storeId;
        
        @Schema(description = "평균 평점")
        private Double averageRating;
        
        @Schema(description = "총 리뷰 수")
        private Long reviewCount;
        
        @Schema(description = "상위 리뷰 목록")
        private List<ReviewInfo> topReviews;

        public static StoreRating from(Long storeId, Double averageRating, Long reviewCount, List<Review> topReviews) {
            List<ReviewInfo> topReviewInfos = topReviews.stream()
                    .map(ReviewInfo::from)
                    .toList();
            
            return StoreRating.builder()
                    .storeId(storeId)
                    .averageRating(averageRating != null ? Math.round(averageRating * 10.0) / 10.0 : 0.0)
                    .reviewCount(reviewCount != null ? reviewCount : 0L)
                    .topReviews(topReviewInfos)
                    .build();
        }
    }

    @Getter
    @Builder
    @Schema(description = "리뷰 작성 응답")
    public static class CreateReviewResponse {
        
        @Schema(description = "생성된 리뷰 ID")
        private Long reviewId;
        
        @Schema(description = "리뷰 정보")
        private ReviewInfo reviewInfo;

        public static CreateReviewResponse from(Review review) {
            return CreateReviewResponse.builder()
                    .reviewId(review.getId())
                    .reviewInfo(ReviewInfo.from(review))
                    .build();
        }
    }
}
