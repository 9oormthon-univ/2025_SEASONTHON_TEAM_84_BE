package com.example.demo.presentation.review.controller;

import com.example.demo.application.review.*;
import com.example.demo.domain.member.entity.Member;
import com.example.demo.infrastructure.exception.payload.dto.ApiResponseDto;
import com.example.demo.infrastructure.security.aop.CurrentMember;
import com.example.demo.presentation.review.dto.ReviewRequest;
import com.example.demo.presentation.review.dto.ReviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "03. Review", description = "리뷰 관련 API")
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final CreateReviewUseCase createReviewUseCase;
    private final GetStoreReviewsUseCase getStoreReviewsUseCase;
    private final UpdateReviewUseCase updateReviewUseCase;
    private final DeleteReviewUseCase deleteReviewUseCase;

    @Operation(
            summary = "리뷰 작성",
            description = "특정 업소에 대한 리뷰를 작성합니다. 한 업소당 한 개의 리뷰만 작성 가능합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "리뷰 작성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 (중복 리뷰, 유효하지 않은 평점 등)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "업소를 찾을 수 없음")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponseDto<ReviewResponse.CreateReviewResponse> createReview(
            @Parameter(hidden = true) @CurrentMember Member currentMember,
            @Valid @RequestBody ReviewRequest.CreateReview request
    ) {
        ReviewResponse.CreateReviewResponse response = createReviewUseCase.execute(currentMember, request);
        return ApiResponseDto.onSuccess(response);
    }

    @Operation(
            summary = "업소 리뷰 목록 조회",
            description = "특정 업소의 리뷰 목록을 페이징으로 조회합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "리뷰 목록 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "업소를 찾을 수 없음")
    })
    @GetMapping("/stores/{storeId}")
    public ApiResponseDto<ReviewResponse.ReviewList> getStoreReviews(
            @PathVariable Long storeId,
            @RequestBody @Valid ReviewRequest.GetReviews request
    ) {
        ReviewResponse.ReviewList response = getStoreReviewsUseCase.execute(storeId, request);
        return ApiResponseDto.onSuccess(response);
    }

    @Operation(
            summary = "업소 평점 정보 조회",
            description = "특정 업소의 평균 평점, 총 리뷰 수, 상위 리뷰를 조회합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "평점 정보 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "업소를 찾을 수 없음")
    })
    @GetMapping("/stores/{storeId}/rating")
    public ApiResponseDto<ReviewResponse.StoreRating> getStoreRating(
            @PathVariable Long storeId,
            @RequestParam(defaultValue = "3") @Parameter(description = "상위 리뷰 개수", example = "3") int topReviewLimit
    ) {
        ReviewResponse.StoreRating response = getStoreReviewsUseCase.executeStoreRating(storeId, topReviewLimit);
        return ApiResponseDto.onSuccess(response);
    }

    @Operation(
            summary = "리뷰 수정",
            description = "본인이 작성한 리뷰를 수정합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "리뷰 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 평점 등)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음 (타인의 리뷰)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{reviewId}")
    public ApiResponseDto<ReviewResponse.ReviewInfo> updateReview(
            @Parameter(hidden = true) @CurrentMember Member currentMember,
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewRequest.UpdateReview request
    ) {
        ReviewResponse.ReviewInfo response = updateReviewUseCase.execute(currentMember, reviewId, request);
        return ApiResponseDto.onSuccess(response);
    }

    @Operation(
            summary = "리뷰 삭제",
            description = "본인이 작성한 리뷰를 삭제합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "리뷰 삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음 (타인의 리뷰)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/{reviewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReview(
            @Parameter(hidden = true) @CurrentMember Member currentMember,
            @PathVariable Long reviewId
    ) {
        deleteReviewUseCase.execute(currentMember, reviewId);
    }
}
