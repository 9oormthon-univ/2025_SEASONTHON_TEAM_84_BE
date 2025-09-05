package com.example.demo.domain.store.adaptor;

import com.example.demo.domain.store.entity.BusinessType;
import com.example.demo.domain.store.entity.Store;
import com.example.demo.domain.store.repository.StoreRepository;
import com.example.demo.infrastructure.annotation.adaptor.Adaptor;
import com.example.demo.infrastructure.exception.object.domain.StoreHandler;
import com.example.demo.infrastructure.exception.payload.code.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Store 도메인의 조회 전용 어댑터
 * 복잡한 조회 로직과 예외 처리를 담당
 */
@Adaptor
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StoreAdaptor {

    private final StoreRepository storeRepository;

    /**
     * ID로 업소 조회
     */
    public Store queryById(Long storeId) {
        return storeRepository.findById(storeId)
            .orElseThrow(() -> new StoreHandler(ErrorStatus.STORE_NOT_FOUND));
    }

    /**
     * 업소명으로 조회
     */
    public Store queryByStoreName(String storeName) {
        return storeRepository.findByStoreName(storeName)
            .orElseThrow(() -> new StoreHandler(ErrorStatus.STORE_NOT_FOUND));
    }

    /**
     * 연락처로 조회
     */
    public Store queryByContactNumber(String contactNumber) {
        return storeRepository.findByContactNumber(contactNumber)
            .orElseThrow(() -> new StoreHandler(ErrorStatus.STORE_NOT_FOUND));
    }

    /**
     * 업소명 포함 검색 (페이징)
     */
    public Page<Store> queryByStoreNameContaining(String storeName, Pageable pageable) {
        return storeRepository.findByStoreNameContainingAndIsActiveTrue(storeName, pageable);
    }

    /**
     * 업종별 조회 (페이징)
     */
    public Page<Store> queryByBusinessType(BusinessType businessType, Pageable pageable) {
        return storeRepository.findByBusinessTypeAndIsActiveTrue(businessType, pageable);
    }

    /**
     * 지역별 조회 (시도, 시군)
     */
    public Page<Store> queryByRegion(String sido, String sigun, Pageable pageable) {
        return storeRepository.findByRegion(sido, sigun, pageable);
    }

    /**
     * 시도별 조회
     */
    public Page<Store> queryBySido(String sido, Pageable pageable) {
        return storeRepository.findBySido(sido, pageable);
    }

    /**
     * 좌표 기반 반경 검색
     */
    public Page<Store> queryStoresWithinRadius(Double latitude, Double longitude, 
                                             Double radiusKm, Pageable pageable) {
        validateCoordinates(latitude, longitude);
        validateRadius(radiusKm);
        
        return storeRepository.findStoresWithinRadius(latitude, longitude, radiusKm, pageable);
    }

    /**
     * 복합 검색: 업종 + 지역
     */
    public Page<Store> queryByBusinessTypeAndRegion(BusinessType businessType, 
                                                   String sido, String sigun, 
                                                   Pageable pageable) {
        return storeRepository.findByBusinessTypeAndRegion(businessType, sido, sigun, pageable);
    }

    /**
     * 복합 검색: 업소명 + 업종 + 지역
     */
    public Page<Store> queryByStoreNameAndBusinessTypeAndSido(String storeName,
                                                             BusinessType businessType,
                                                             String sido,
                                                             Pageable pageable) {
        return storeRepository.findByStoreNameAndBusinessTypeAndSido(
            storeName, businessType, sido, pageable);
    }

    /**
     * 활성화된 모든 업소 조회
     */
    public Page<Store> queryActiveStores(Pageable pageable) {
        return storeRepository.findByIsActiveTrue(pageable);
    }

    /**
     * 좌표 정보가 있는 업소 조회 (지도 표시용)
     */
    public List<Store> queryStoresWithCoordinates() {
        return storeRepository.findStoresWithCoordinates();
    }

    /**
     * 좌표 정보가 없는 업소 조회 (좌표 보완 필요)
     */
    public List<Store> queryStoresWithoutCoordinates() {
        return storeRepository.findStoresWithoutCoordinates();
    }

    /**
     * 업종별 업소 수 통계
     */
    public List<Object[]> queryStoreCountByBusinessType() {
        return storeRepository.countByBusinessType();
    }

    /**
     * 지역별 업소 수 통계
     */
    public List<Object[]> queryStoreCountByRegion() {
        return storeRepository.countByRegion();
    }

    /**
     * 업소 존재 여부 확인 (ID)
     */
    public boolean existsById(Long storeId) {
        return storeRepository.existsById(storeId);
    }

    /**
     * 업소 존재 여부 확인 (업소명)
     */
    public boolean existsByStoreName(String storeName) {
        return storeRepository.findByStoreName(storeName).isPresent();
    }

    /**
     * 업소 존재 여부 확인 (연락처)
     */
    public boolean existsByContactNumber(String contactNumber) {
        return storeRepository.findByContactNumber(contactNumber).isPresent();
    }

    // === 검증 메서드들 ===

    /**
     * 좌표 유효성 검증
     */
    private void validateCoordinates(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            throw new StoreHandler(ErrorStatus.INVALID_COORDINATES);
        }
        
        if (latitude < -90.0 || latitude > 90.0) {
            throw new StoreHandler(ErrorStatus.INVALID_LATITUDE);
        }
        
        if (longitude < -180.0 || longitude > 180.0) {
            throw new StoreHandler(ErrorStatus.INVALID_LONGITUDE);
        }
    }

    /**
     * 반경 유효성 검증
     */
    private void validateRadius(Double radiusKm) {
        if (radiusKm == null || radiusKm <= 0) {
            throw new StoreHandler(ErrorStatus.INVALID_RADIUS);
        }
        
        if (radiusKm > 100.0) { // 최대 100km로 제한
            throw new StoreHandler(ErrorStatus.RADIUS_TOO_LARGE);
        }
    }
}
