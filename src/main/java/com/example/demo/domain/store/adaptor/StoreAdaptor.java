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

@Adaptor
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StoreAdaptor {

    private final StoreRepository storeRepository;

    public Store queryById(Long storeId) {
        return storeRepository.findById(storeId)
            .orElseThrow(() -> new StoreHandler(StoreErrorStatus.STORE_NOT_FOUND));
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
