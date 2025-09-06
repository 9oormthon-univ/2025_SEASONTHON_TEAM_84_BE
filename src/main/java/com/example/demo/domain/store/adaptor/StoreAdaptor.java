package com.example.demo.domain.store.adaptor;

import com.example.demo.domain.store.entity.Category;
import com.example.demo.domain.store.entity.Store;
import com.example.demo.domain.store.exception.StoreErrorStatus;
import com.example.demo.domain.store.exception.StoreHandler;
import com.example.demo.domain.store.repository.StoreRepository;
import com.example.demo.domain.store.validator.StoreValidator;
import com.example.demo.infrastructure.annotation.adaptor.Adaptor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;

@Adaptor
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StoreAdaptor {

    private final StoreRepository storeRepository;

    public Store queryById(Long storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreHandler(StoreErrorStatus.STORE_NOT_FOUND));
    }

    public Store queryByIdFetchMenu(Long storeId) {
        return storeRepository.findByIdFetchMenu(storeId).orElseThrow(
                () -> new StoreHandler(StoreErrorStatus.STORE_NOT_FOUND)
        );
    }

    public Store queryByStoreName(String storeName) {
        return storeRepository.findByStoreName(storeName)
                .orElseThrow(() -> new StoreHandler(StoreErrorStatus.STORE_NOT_FOUND));
    }

    public Store queryByContactNumber(String contactNumber) {
        return storeRepository.findByContactNumber(contactNumber)
                .orElseThrow(() -> new StoreHandler(StoreErrorStatus.STORE_NOT_FOUND));
    }

    public Page<Store> queryByStoreNameContaining(String storeName, Pageable pageable) {
        return storeRepository.findByStoreNameContainingAndIsActiveTrue(storeName, pageable);
    }

    public Page<Store> queryByBusinessType(Category businessType, Pageable pageable) {
        return storeRepository.findByCategoryAndIsActiveTrue(businessType, pageable);
    }

    public Page<Store> queryByRegion(String sido, String sigun, Pageable pageable) {
        return storeRepository.findByRegion(sido, sigun, pageable);
    }

    public Page<Store> queryBySido(String sido, Pageable pageable) {
        return storeRepository.findBySido(sido, pageable);
    }

    public Page<Store> queryStoresWithinRadius(Double latitude, Double longitude,
                                               Double radiusKm, Pageable pageable) {
        StoreValidator.validateCoordinates(latitude, longitude);
        StoreValidator.validateRadius(radiusKm);

        return storeRepository.findStoresWithinRadius(latitude, longitude, radiusKm, pageable);
    }

    /**
     * 반경 내 업소를 거리순으로 정렬된 ID 기준으로 메뉴까지 fetch join 하여 반환
     * 반환 순서는 거리 오름차순을 보존한다.
     */
    public List<Store> queryStoresWithinRadiusWithMenus(Double latitude,
                                                        Double longitude,
                                                        Double radiusKm,
                                                        int limit) {
        StoreValidator.validateCoordinates(latitude, longitude);
        StoreValidator.validateRadius(radiusKm);

        // 거리순으로 정렬된 페이지를 받아 상위 limit만 사용
        List<Store> pageContent = storeRepository
            .findStoresWithinRadius(latitude, longitude, radiusKm, Pageable.ofSize(limit))
            .getContent();

        if (pageContent.isEmpty()) {
            return List.of();
        }

        List<Long> orderedIds = pageContent.stream()
            .map(Store::getId)
            .toList();

        // 메뉴 fetch join으로 한번에 조회
        List<Store> fetched = storeRepository.findAllByIdInFetchMenus(orderedIds);

        // IN 조회 결과의 순서를 거리순으로 재정렬
        Map<Long, Integer> orderIndex = new HashMap<>();
        for (int i = 0; i < orderedIds.size(); i++) {
            orderIndex.put(orderedIds.get(i), i);
        }
        fetched.sort(Comparator.comparingInt(s -> orderIndex.getOrDefault(s.getId(), Integer.MAX_VALUE)));
        return fetched;
    }

    public Page<Store> queryByBusinessTypeAndRegion(Category businessType,
                                                    String sido, String sigun,
                                                    Pageable pageable) {
        return storeRepository.findByCategoryTypeAndRegion(businessType, sido, sigun, pageable);
    }

    public Page<Store> queryByStoreNameAndBusinessTypeAndSido(String storeName,
                                                              Category businessType,
                                                              String sido,
                                                              Pageable pageable) {
        return storeRepository.findByStoreNameAndCategoryAndSido(
                storeName, businessType, sido, pageable);
    }

    public Page<Store> queryActiveStores(Pageable pageable) {
        return storeRepository.findByIsActiveTrue(pageable);
    }

    public List<Store> queryStoresWithCoordinates() {
        return storeRepository.findStoresWithCoordinates();
    }

    /**
     * 좌표가 있는 활성 업소를 메뉴까지 fetch join으로 조회
     */
    public List<Store> queryStoresWithCoordinatesFetchMenus() {
        return storeRepository.findStoresWithCoordinatesFetchMenus();
    }

    public List<Store> queryStoresWithoutCoordinates() {
        return storeRepository.findStoresWithoutCoordinates();
    }

    public List<Object[]> queryStoreCountByBusinessType() {
        return storeRepository.countByCategory();
    }

    public List<Object[]> queryStoreCountByRegion() {
        return storeRepository.countByRegion();
    }

    public boolean existsById(Long storeId) {
        return storeRepository.existsById(storeId);
    }

    public boolean existsByStoreName(String storeName) {
        return storeRepository.findByStoreName(storeName).isPresent();
    }

    public boolean existsByContactNumber(String contactNumber) {
        return storeRepository.findByContactNumber(contactNumber).isPresent();
    }

    public void validateStoreNameNotExists(String storeName) {
        if (storeRepository.findByStoreName(storeName).isPresent()) {
            throw new StoreHandler(StoreErrorStatus.STORE_ALREADY_EXISTS);
        }
    }

    public void validateContactNumberNotExists(String contactNumber) {
        if (storeRepository.findByContactNumber(contactNumber).isPresent()) {
            throw new StoreHandler(StoreErrorStatus.STORE_ALREADY_EXISTS);
        }
    }
}
