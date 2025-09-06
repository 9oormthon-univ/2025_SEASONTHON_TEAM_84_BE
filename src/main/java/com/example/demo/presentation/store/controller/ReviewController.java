package com.example.demo.presentation.store.controller;

import com.example.demo.domain.review.entity.Review;
import com.example.demo.domain.review.service.ReviewService;
import com.example.demo.infrastructure.exception.payload.dto.ApiResponseDto;
import com.example.demo.presentation.store.dto.ReviewRankingDto;
import com.example.demo.presentation.store.dto.ReviewRequestDto;
import com.example.demo.presentation.store.dto.ReviewResponseDto;
import com.example.demo.presentation.store.dto.ReviewUpdateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api//v1/reviews")
@Tag(name = "Review", description = "업소 리뷰 및 별점 관련 API")
public class ReviewController {
    private final ReviewService reviewService;

    @Operation(summary = "리뷰 작성", description = "업소에 대한 리뷰를 작성합니다.")
    @PostMapping
    public ResponseEntity<ReviewResponseDto> createReview(@RequestBody ReviewRequestDto request) {
        Review review = reviewService.createReview(request);
        return ResponseEntity.ok(toDto(review));
    }

    @Operation(summary = "리뷰 수정")
    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDto> updateReview(
            @PathVariable Long reviewId,
            @RequestBody ReviewUpdateDto request) {
        Review review = reviewService.updateReview(reviewId, request.getContent(), request.getRating());
        return ResponseEntity.ok(toDto(review));
    }

    @Operation(summary = "리뷰 삭제")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "업소 리뷰 조회", description = "특정 업소의 모든 리뷰를 조회합니다.")
    @GetMapping("/store/{storeId}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "업소를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    })
    public ResponseEntity<List<ReviewResponseDto>> getReviewsByStore(@PathVariable Long storeId) {
        List<ReviewResponseDto> reviews = reviewService.getReviewsByStore(storeId).stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "업소 평균 별점 조회", description = "특정 업소의 평균 별점을 조회합니다.")
    @GetMapping("/store/{storeId}/average-rating")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long storeId) {
        return ResponseEntity.ok(reviewService.getAverageRating(storeId));
    }

    @Operation(summary = "작성자 리뷰 목록")
    @GetMapping("/writer/{writerId}")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsByWriter(@PathVariable Long writerId) {
        List<ReviewResponseDto> reviews = reviewService.getReviewsByWriter(writerId).stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "전체 업소 랭킹", description = "모든 업소의 평균 별점 순위를 조회합니다.")
    @GetMapping("/ranking")
    public ResponseEntity<List<ReviewRankingDto>> getAllRanking() {
        List<ReviewRankingDto> ranking = reviewService.getAllStoreRanking();
        return ResponseEntity.ok(ranking);
    }

    private ReviewResponseDto toDto(Review review) {
        return ReviewResponseDto.builder()
                .id(review.getId())
                .content(review.getContent())
                .writerId(review.getWriterId())
                .rating(review.getRating())
                .storeName(review.getStore().getStoreName())
                .createdDate(review.getCreatedDate())
                .lastModifiedDate(review.getLastModifiedDate())
                .build();
    }
}
