package com.example.demo.presentation.store.controller;

import com.example.demo.application.store.GetNearbyStoresUseCase;
import com.example.demo.application.store.GetStoreDetailUseCase;
import com.example.demo.infrastructure.exception.payload.dto.ApiResponseDto;
import com.example.demo.presentation.store.dto.StoreRequest;
import com.example.demo.presentation.store.dto.StoreResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Store", description = "착한가격업소 관리 API")
@RestController
@RequestMapping("/stores")
@RequiredArgsConstructor
public class StoreController {

    private final GetStoreDetailUseCase getStoreDetailUseCase;
    private final GetNearbyStoresUseCase getNearbyStoresUseCase;

    @Operation(summary = "업소 상세 조회", description = "업소 ID로 상세 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "업소를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    })
    @GetMapping("/{storeId}")
    public ResponseEntity<ApiResponseDto<StoreResponse.StoreDetailWithReviews>> getStore(
            @Parameter(description = "업소 ID", required = true) @PathVariable Long storeId) {
        StoreResponse.StoreDetailWithReviews response = getStoreDetailUseCase.execute(storeId);
        return ResponseEntity.ok(ApiResponseDto.onSuccess(response));
    }

    @Operation(summary = "반경 내 업소 검색", description = "지정된 좌표를 중심으로 반경 내 업소를 검색합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "검색 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 좌표 또는 반경", content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    })
    @PostMapping("/nearby")
    public ApiResponseDto<StoreResponse.NearbyStoreList> getStoresWithinRadius(
            @Parameter(description = "반경 검색 조건") @ModelAttribute @Valid StoreRequest.GetNearbyStores request) {

        StoreResponse.NearbyStoreList response = getNearbyStoresUseCase.execute(request);
        return ApiResponseDto.onSuccess(response);
    }

}
