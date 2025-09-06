package com.example.demo.application.store;

import com.example.demo.domain.store.adaptor.StoreAdaptor;
import com.example.demo.domain.store.entity.Category;
import com.example.demo.domain.store.entity.Store;
import com.example.demo.domain.store.service.StoreService;
import com.example.demo.domain.store.vo.StoreSearchCondition;
import com.example.demo.infrastructure.annotation.usecase.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Store UseCase
 * 어플리케이션 레이어에서 Store 관련 비즈니스 로직을 조합하고 제어
 */
@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreUseCase {

    private final StoreService storeService;
    private final StoreAdaptor storeAdaptor;

    // === 조회 관련 UseCase ===

    /**
     * 업소 상세 조회
     */
    public Store getStoreById(Long storeId) {
        return storeAdaptor.queryById(storeId);
    }

    /**
     * 업소 목록 조회 (전체)
     */
    public Page<Store> getActiveStores(Pageable pageable) {
        return storeAdaptor.queryActiveStores(pageable);
    }

    /**
     * 업소 검색 (통합)
     */
    public Page<Store> searchStores(StoreSearchCondition condition, Pageable pageable) {
        // 검색 조건에 따른 분기 처리
        if (condition.isEmpty()) {
            return storeAdaptor.queryActiveStores(pageable);
        }

        // 좌표 기반 검색 우선
        if (condition.hasLocationCondition()) {
            return storeAdaptor.queryStoresWithinRadius(
                condition.getLatitude(), 
                condition.getLongitude(), 
                condition.getRadiusKm(), 
                pageable
            );
        }

        // 복합 검색 조건 처리
        if (condition.isComplexSearch()) {
            return handleComplexSearch(condition, pageable);
        }

        // 단일 조건 검색
        return handleSingleConditionSearch(condition, pageable);
    }

    /**
     * 업종별 업소 조회
     */
    public Page<Store> getStoresByBusinessType(Category businessType, Pageable pageable) {
        return storeAdaptor.queryByBusinessType(businessType, pageable);
    }

    /**
     * 지역별 업소 조회
     */
    public Page<Store> getStoresByRegion(String sido, String sigun, Pageable pageable) {
        return storeAdaptor.queryByRegion(sido, sigun, pageable);
    }

    /**
     * 좌표 기반 반경 검색
     */
    public Page<Store> getStoresWithinRadius(Double latitude, Double longitude, 
                                           Double radiusKm, Pageable pageable) {
        return storeAdaptor.queryStoresWithinRadius(latitude, longitude, radiusKm, pageable);
    }

    /**
     * 지도 표시용 업소 조회 (좌표 정보가 있는 업소만)
     */
    public List<Store> getStoresForMap() {
        return storeAdaptor.queryStoresWithCoordinates();
    }

    /**
     * 통계 정보 조회 - 지역별 업소 수
     */
    public List<Object[]> getStoreStatsByRegion() {
        return storeAdaptor.queryStoreCountByRegion();
    }

    // === 생성/수정 관련 UseCase ===

    /**
     * 업소 생성
     */
    @Transactional
    public Store createStore(String storeName, Category businessType, String contactNumber,
                           String sido, String sigun, String fullAddress, 
                           Double latitude, Double longitude,
                           String majorCategory, String subCategory) {
        return storeService.createStore(
            storeName, businessType, contactNumber,
            sido, sigun, fullAddress, latitude, longitude,
            majorCategory, subCategory
        );
    }

    /**
     * 업소 정보 수정
     */
    @Transactional
    public Store updateStore(Long storeId, String storeName, Category businessType, 
                           String contactNumber, String majorCategory, String subCategory) {
        return storeService.updateStore(storeId, storeName, businessType, contactNumber, majorCategory, subCategory);
    }

    /**
     * 업소 좌표 정보 업데이트
     */
    @Transactional
    public Store updateStoreCoordinates(Long storeId, Double latitude, Double longitude) {
        return storeService.updateStoreCoordinates(storeId, latitude, longitude);
    }

    /**
     * 메뉴 추가
     */
    @Transactional
    public Store addMenu(Long storeId, String menuName, BigDecimal price) {
        return storeService.addMenu(storeId, menuName, price);
    }

    /**
     * 업소 활성화/비활성화
     */
    @Transactional
    public Store toggleStoreStatus(Long storeId) {
        return storeService.toggleStoreStatus(storeId);
    }

    /**
     * 업소 삭제 (논리 삭제)
     */
    @Transactional
    public void deleteStore(Long storeId) {
        storeService.deleteStore(storeId);
    }

    /**
     * 엑셀 데이터 일괄 등록
     */
    @Transactional
    public List<Store> bulkCreateStoresFromExcel(List<StoreService.StoreExcelData> excelDataList) {
        return storeService.bulkCreateStores(excelDataList);
    }

    // === 내부 헬퍼 메서드들 ===

    /**
     * 복합 검색 조건 처리
     */
    private Page<Store> handleComplexSearch(StoreSearchCondition condition, Pageable pageable) {
        if (condition.hasBusinessTypeCondition() && condition.hasRegionCondition()) {
            return storeAdaptor.queryByBusinessTypeAndRegion(
                condition.getBusinessType(),
                condition.getSido(),
                condition.getSigun(),
                pageable
            );
        }

        if (condition.hasStoreNameCondition() && 
            condition.hasBusinessTypeCondition() && 
            condition.getSido() != null) {
            return storeAdaptor.queryByStoreNameAndBusinessTypeAndSido(
                condition.getStoreName(),
                condition.getBusinessType(),
                condition.getSido(),
                pageable
            );
        }

        // 기본 단일 조건으로 fallback
        return handleSingleConditionSearch(condition, pageable);
    }

    /**
     * 단일 조건 검색 처리
     */
    private Page<Store> handleSingleConditionSearch(StoreSearchCondition condition, Pageable pageable) {
        if (condition.hasStoreNameCondition()) {
            return storeAdaptor.queryByStoreNameContaining(condition.getStoreName(), pageable);
        }

        if (condition.hasBusinessTypeCondition()) {
            return storeAdaptor.queryByBusinessType(condition.getBusinessType(), pageable);
        }

        if (condition.hasRegionCondition()) {
            return storeAdaptor.queryByRegion(
                condition.getSido(), 
                condition.getSigun(), 
                pageable
            );
        }

        // 모든 조건이 없으면 전체 조회
        return storeAdaptor.queryActiveStores(pageable);
    }
}
