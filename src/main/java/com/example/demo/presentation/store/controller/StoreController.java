package com.example.demo.presentation.store.controller;

import com.example.demo.application.store.StoreUseCase;
import com.example.demo.domain.store.entity.Category;
import com.example.demo.domain.store.entity.Store;
import com.example.demo.domain.store.service.StoreExcelService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Store", description = "착한가격업소 관리 API")
@RestController
@RequestMapping("/api/v1/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreUseCase storeUseCase;
    private final StoreExcelService storeExcelService;

    @Operation(summary = "업소 목록 조회", description = "활성화된 모든 업소 목록을 페이징으로 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    })
    @GetMapping
    public ResponseEntity<ApiResponseDto<Page<StoreResponse.StoreInfo>>> getStores(
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Store> stores = storeUseCase.getActiveStores(pageable);
        Page<StoreResponse.StoreInfo> response = stores.map(StoreResponse.StoreInfo::from);

        return ResponseEntity.ok(ApiResponseDto.onSuccess(response));
    }

    @Operation(summary = "업소 상세 조회", description = "업소 ID로 상세 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "업소를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    })
    @GetMapping("/{storeId}")
    public ResponseEntity<ApiResponseDto<StoreResponse.StoreDetail>> getStore(
            @Parameter(description = "업소 ID", required = true) @PathVariable Long storeId) {

        Store store = storeUseCase.getStoreById(storeId);
        StoreResponse.StoreDetail response = StoreResponse.StoreDetail.from(store);
        
        return ResponseEntity.ok(ApiResponseDto.onSuccess(response));
    }

    @Operation(summary = "업소 검색", description = "다양한 조건으로 업소를 검색합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "검색 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 검색 조건", content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    })
    @GetMapping("/search")
    public ResponseEntity<ApiResponseDto<Page<StoreResponse.StoreInfo>>> searchStores(
            @Parameter(description = "검색 조건") @ModelAttribute @Valid StoreRequest.SearchStore searchRequest,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Store> stores = storeUseCase.searchStores(searchRequest.toSearchCondition(), pageable);
        Page<StoreResponse.StoreInfo> response = stores.map(StoreResponse.StoreInfo::from);
        
        return ResponseEntity.ok(ApiResponseDto.onSuccess(response));
    }

    @Operation(summary = "반경 내 업소 검색", description = "지정된 좌표를 중심으로 반경 내 업소를 검색합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "검색 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 좌표 또는 반경", content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    })
    @GetMapping("/radius")
    public ResponseEntity<ApiResponseDto<Page<StoreResponse.StoreInfo>>> getStoresWithinRadius(
            @Parameter(description = "반경 검색 조건") @ModelAttribute @Valid StoreRequest.RadiusSearch radiusRequest,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Store> stores = storeUseCase.getStoresWithinRadius(
            radiusRequest.getLatitude(),
            radiusRequest.getLongitude(),
            radiusRequest.getRadiusKm(),
            pageable
        );
        Page<StoreResponse.StoreInfo> response = stores.map(StoreResponse.StoreInfo::from);
        
        return ResponseEntity.ok(ApiResponseDto.onSuccess(response));
    }

    @Operation(summary = "업종별 업소 조회", description = "특정 업종의 업소 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 업종", content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    })
    @GetMapping("/business-type/{businessType}")
    public ResponseEntity<ApiResponseDto<Page<StoreResponse.StoreInfo>>> getStoresByBusinessType(
            @Parameter(description = "업종", required = true) @PathVariable Category businessType,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Store> stores = storeUseCase.getStoresByBusinessType(businessType, pageable);
        Page<StoreResponse.StoreInfo> response = stores.map(StoreResponse.StoreInfo::from);
        
        return ResponseEntity.ok(ApiResponseDto.onSuccess(response));
    }

    @Operation(summary = "지역별 업소 조회", description = "시도, 시군으로 업소를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 지역 정보", content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    })
    @GetMapping("/region")
    public ResponseEntity<ApiResponseDto<Page<StoreResponse.StoreInfo>>> getStoresByRegion(
            @Parameter(description = "시도", required = true) @RequestParam String sido,
            @Parameter(description = "시군") @RequestParam(required = false) String sigun,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Store> stores = storeUseCase.getStoresByRegion(sido, sigun, pageable);
        Page<StoreResponse.StoreInfo> response = stores.map(StoreResponse.StoreInfo::from);
        
        return ResponseEntity.ok(ApiResponseDto.onSuccess(response));
    }

    @Operation(summary = "지도용 업소 정보 조회", description = "지도 표시를 위한 좌표가 있는 업소 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/map")
    public ResponseEntity<ApiResponseDto<List<StoreResponse.MapStoreInfo>>> getStoresForMap() {

        List<Store> stores = storeUseCase.getStoresForMap();
        List<StoreResponse.MapStoreInfo> response = stores.stream()
            .map(StoreResponse.MapStoreInfo::from)
            .toList();
        
        return ResponseEntity.ok(ApiResponseDto.onSuccess(response));
    }

    @Operation(summary = "업소 생성", description = "새로운 업소를 등록합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
        @ApiResponse(responseCode = "409", description = "이미 존재하는 업소", content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    })
    @PostMapping
    public ResponseEntity<ApiResponseDto<StoreResponse.StoreDetail>> createStore(
            @Parameter(description = "업소 생성 정보", required = true) @RequestBody @Valid StoreRequest.CreateStore createRequest) {
        
        Store store = storeUseCase.createStore(
            createRequest.getStoreName(),
            createRequest.getBusinessType(),
            createRequest.getContactNumber(),
            createRequest.getSido(),
            createRequest.getSigun(),
            createRequest.getFullAddress(),
            createRequest.getLatitude(),
            createRequest.getLongitude(),
            createRequest.getMajorCategory(),
            createRequest.getSubCategory()
        );
        
        StoreResponse.StoreDetail response = StoreResponse.StoreDetail.from(store);
        return ResponseEntity.ok(ApiResponseDto.onSuccess(response));
    }

    @Operation(summary = "업소 정보 수정", description = "업소의 기본 정보를 수정합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "업소를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    })
    @PutMapping("/{storeId}")
    public ResponseEntity<ApiResponseDto<StoreResponse.StoreDetail>> updateStore(
            @Parameter(description = "업소 ID", required = true) @PathVariable Long storeId,
            @Parameter(description = "업소 수정 정보", required = true) @RequestBody @Valid StoreRequest.UpdateStore updateRequest) {
        
        Store store = storeUseCase.updateStore(
            storeId,
            updateRequest.getStoreName(),
            updateRequest.getBusinessType(),
            updateRequest.getContactNumber(),
            updateRequest.getMajorCategory(),
            updateRequest.getSubCategory()
        );
        
        StoreResponse.StoreDetail response = StoreResponse.StoreDetail.from(store);
        return ResponseEntity.ok(ApiResponseDto.onSuccess(response));
    }

    @Operation(summary = "업소 좌표 수정", description = "업소의 좌표 정보를 수정합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 좌표 정보", content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "업소를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    })
    @PatchMapping("/{storeId}/coordinates")
    public ResponseEntity<ApiResponseDto<StoreResponse.StoreDetail>> updateStoreCoordinates(
            @Parameter(description = "업소 ID", required = true) @PathVariable Long storeId,
            @Parameter(description = "좌표 정보", required = true) @RequestBody @Valid StoreRequest.UpdateCoordinates coordinatesRequest) {
        
        Store store = storeUseCase.updateStoreCoordinates(
            storeId,
            coordinatesRequest.getLatitude(),
            coordinatesRequest.getLongitude()
        );
        
        StoreResponse.StoreDetail response = StoreResponse.StoreDetail.from(store);
        return ResponseEntity.ok(ApiResponseDto.onSuccess(response));
    }

    @Operation(summary = "메뉴 추가", description = "업소에 새로운 메뉴를 추가합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "추가 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 메뉴 정보", content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "업소를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    })
    @PostMapping("/{storeId}/menus")
    public ResponseEntity<ApiResponseDto<StoreResponse.StoreDetail>> addMenu(
            @Parameter(description = "업소 ID", required = true) @PathVariable Long storeId,
            @Parameter(description = "메뉴 정보", required = true) @RequestBody @Valid StoreRequest.AddMenu menuRequest) {
        
        Store store = storeUseCase.addMenu(
            storeId,
            menuRequest.getMenuName(),
            menuRequest.getPrice()
        );
        
        StoreResponse.StoreDetail response = StoreResponse.StoreDetail.from(store);
        return ResponseEntity.ok(ApiResponseDto.onSuccess(response));
    }

    @Operation(summary = "업소 상태 변경", description = "업소의 활성화/비활성화 상태를 변경합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "변경 성공"),
        @ApiResponse(responseCode = "404", description = "업소를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    })
    @PatchMapping("/{storeId}/toggle-status")
    public ResponseEntity<ApiResponseDto<StoreResponse.StoreDetail>> toggleStoreStatus(
            @Parameter(description = "업소 ID", required = true) @PathVariable Long storeId) {
        
        Store store = storeUseCase.toggleStoreStatus(storeId);
        StoreResponse.StoreDetail response = StoreResponse.StoreDetail.from(store);
        
        return ResponseEntity.ok(ApiResponseDto.onSuccess(response));
    }

    @Operation(summary = "업소 삭제", description = "업소를 삭제합니다(논리 삭제).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "삭제 성공"),
        @ApiResponse(responseCode = "404", description = "업소를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    })
    @DeleteMapping("/{storeId}")
    public ResponseEntity<ApiResponseDto<Void>> deleteStore(
            @Parameter(description = "업소 ID", required = true) @PathVariable Long storeId) {
        
        storeUseCase.deleteStore(storeId);
        return ResponseEntity.ok(ApiResponseDto.onSuccess(null));
    }

    @Operation(summary = "업소 데이터 일괄 업로드", description = "Excel 또는 CSV 파일을 통해 업소 데이터를 일괄 등록합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "업로드 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 파일 형식 또는 데이터", content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    })
    @PostMapping("/upload")
    public ResponseEntity<ApiResponseDto<String>> uploadStoresFile(
            @Parameter(description = "업소 데이터 파일 (Excel 또는 CSV)", required = true) 
            @RequestParam("file") MultipartFile file) {
        
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                .body(ApiResponseDto.onFailure(4000, "업로드할 파일을 선택해주세요.", null));
        }
        
        storeExcelService.importStoresFromFile(file);
        
        return ResponseEntity.ok(ApiResponseDto.onSuccess("파일 업로드가 완료되었습니다."));
    }
}
