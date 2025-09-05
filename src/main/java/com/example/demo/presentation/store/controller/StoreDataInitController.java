package com.example.demo.presentation.store.controller;

import com.example.demo.domain.store.service.StoreExcelService;
import com.example.demo.infrastructure.exception.payload.dto.ApiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "StoreDataInit", description = "초기 데이터 세팅용 업로드 API")
@RestController
@RequestMapping("/api/v1/init/stores")
@RequiredArgsConstructor
public class StoreDataInitController {

    private final StoreExcelService storeExcelService;

    @Operation(summary = "초기 데이터 업로드", description = "CSV/XLSX 파일을 업로드하여 초기 데이터를 일괄 등록합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "업로드 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 파일 형식 또는 데이터", content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    })
    @PostMapping("/upload")
    public ResponseEntity<ApiResponseDto<String>> seedFromFile(
        @Parameter(description = "초기 데이터 파일 (CSV 또는 Excel)", required = true)
        @RequestParam("file") @NotNull MultipartFile file
    ) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                .body(ApiResponseDto.onFailure(4000, "업로드할 파일을 선택해주세요.", null));
        }

        storeExcelService.importStoresFromFile(file);
        return ResponseEntity.ok(ApiResponseDto.onSuccess("초기 데이터 업로드가 완료되었습니다."));
    }
}


