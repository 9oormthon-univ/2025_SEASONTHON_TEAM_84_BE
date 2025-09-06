package com.example.demo.presentation.store.controller;

import com.example.demo.domain.Favorite.service.FavoriteService;
import com.example.demo.presentation.store.dto.FavoriteResponseDto;
import com.example.demo.presentation.store.dto.FavoriteToggleResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/favorites")
@Tag(name = "Favorite", description = "즐겨찾기 API")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Operation(summary = "즐겨찾기 추가/삭제 토글", description = "즐겨찾기를 추가하거나 삭제합니다")
    @PostMapping("/toggle/{storeId}")
    public ResponseEntity<FavoriteToggleResponseDto> toggleFavorite(
            @PathVariable Long storeId,
            @RequestParam Long memberId) {

        boolean isAdded = favoriteService.toggleFavorite(memberId, storeId);

        return ResponseEntity.ok(FavoriteToggleResponseDto.builder()
                .storeId(storeId)
                .isFavorite(isAdded)
                .message(isAdded ? "즐겨찾기에 추가되었습니다." : "즐겨찾기가 해제되었습니다.")
                .build());
    }

    @Operation(summary = "내 즐겨찾기 목록 조회", description = "사용자의 즐겨찾기 목록을 조회합니다")
    @GetMapping("/my")
    public ResponseEntity<List<FavoriteResponseDto>> getMyFavorites(
            @RequestParam Long memberId) {

        List<FavoriteResponseDto> favorites = favoriteService.getMyFavorites(memberId);
        return ResponseEntity.ok(favorites);
    }

    @Operation(summary = "즐겨찾기 여부 확인", description = "즐겨찾기 여부를 확인합니다")
    @GetMapping("/check/{storeId}")
    public ResponseEntity<Boolean> checkFavorite(
            @PathVariable Long storeId,
            @RequestParam Long memberId) {

        boolean isFavorite = favoriteService.isFavorite(memberId, storeId);
        return ResponseEntity.ok(isFavorite);
    }

    @Operation(summary = "업소의 즐겨찾기 수 조회", description = "업소의 즐겨찾기 수를 조회합니다.")
    @GetMapping("/count/{storeId}")
    public ResponseEntity<Long> getFavoriteCount(@PathVariable Long storeId) {
        long count = favoriteService.getFavoriteCount(storeId);
        return ResponseEntity.ok(count);
    }
}
