package com.example.demo.domain.store.service;

import com.example.demo.domain.store.entity.Address;
import com.example.demo.domain.store.entity.BusinessType;
import com.example.demo.domain.store.entity.Store;
import com.example.demo.domain.store.exception.StoreErrorStatus;
import com.example.demo.domain.store.exception.StoreHandler;
import com.example.demo.domain.store.repository.StoreRepository;
import com.example.demo.infrastructure.exception.payload.code.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Store 도메인 서비스
 * 업소 생성, 수정, 삭제 등의 비즈니스 로직을 담당
 */
@Service
@RequiredArgsConstructor
@Transactional
public class StoreService {

    private final StoreRepository storeRepository;

    /**
     * 업소 생성
     */
    public Store createStore(String storeName, BusinessType businessType, String contactNumber,
                           String sido, String sigun, String fullAddress, 
                           Double latitude, Double longitude) {
        
        // 업소명 중복 검증
        validateStoreNameNotExists(storeName);
        
        // 연락처 중복 검증 (있는 경우만)
        if (contactNumber != null && !contactNumber.trim().isEmpty()) {
            validateContactNumberNotExists(contactNumber);
        }

        // 주소 객체 생성
        Address address = new Address(sido, sigun, fullAddress, latitude, longitude);
        
        // 주소 유효성 검증
        if (!address.isValidAddress()) {
            throw new StoreHandler(StoreErrorStatus.INVALID_COORDINATES);
        }

        // Store 엔티티 생성
        Store store = Store.builder()
            .storeName(storeName.trim())
            .businessType(businessType)
            .contactNumber(contactNumber != null ? contactNumber.trim() : null)
            .address(address)
            .isActive(true)
            .build();

        return storeRepository.save(store);
    }

    /**
     * 업소 정보 수정
     */
    public Store updateStore(Long storeId, String storeName, BusinessType businessType, 
                           String contactNumber) {
        Store store = findStoreById(storeId);
        
        // 업소명 변경 시 중복 검증
        if (storeName != null && !storeName.equals(store.getStoreName())) {
            validateStoreNameNotExists(storeName);
        }
        
        // 연락처 변경 시 중복 검증
        if (contactNumber != null && !contactNumber.equals(store.getContactNumber())) {
            validateContactNumberNotExists(contactNumber);
        }

        Store updatedStore = store.updateStoreInfo(storeName, businessType, contactNumber);
        return storeRepository.save(updatedStore);
    }

    /**
     * 업소 좌표 정보 업데이트
     */
    public Store updateStoreCoordinates(Long storeId, Double latitude, Double longitude) {
        Store store = findStoreById(storeId);
        
        // 좌표 유효성 검증
        validateCoordinates(latitude, longitude);
        
        store.updateCoordinates(latitude, longitude);
        return storeRepository.save(store);
    }

    /**
     * 메뉴 추가
     */
    public Store addMenu(Long storeId, String menuName, BigDecimal price) {
        Store store = findStoreById(storeId);
        
        // 메뉴 정보 검증
        validateMenuInfo(menuName, price);
        
        store.addMenu(menuName, price);
        return storeRepository.save(store);
    }

    /**
     * 업소 활성화/비활성화
     */
    public Store toggleStoreStatus(Long storeId) {
        Store store = findStoreById(storeId);
        store.toggleActiveStatus();
        return storeRepository.save(store);
    }

    /**
     * 업소 삭제 (논리 삭제)
     */
    public void deleteStore(Long storeId) {
        Store store = findStoreById(storeId);
        if (store.isActive()) {
            store.toggleActiveStatus(); // 비활성화
            storeRepository.save(store);
        }
    }

    /**
     * 업소 완전 삭제 (물리 삭제)
     */
    public void permanentDeleteStore(Long storeId) {
        Store store = findStoreById(storeId);
        storeRepository.delete(store);
    }

    /**
     * 엑셀 데이터 일괄 등록
     */
    public List<Store> bulkCreateStores(List<StoreExcelData> excelDataList) {
        return excelDataList.stream()
            .map(this::createStoreFromExcelData)
            .toList();
    }

    // === Private 헬퍼 메서드들 ===

    /**
     * ID로 업소 조회 (내부용)
     */
    private Store findStoreById(Long storeId) {
        return storeRepository.findById(storeId)
            .orElseThrow(() -> new StoreHandler(StoreErrorStatus.STORE_NOT_FOUND));
    }

    /**
     * 업소명 중복 검증
     */
    private void validateStoreNameNotExists(String storeName) {
        if (storeRepository.findByStoreName(storeName).isPresent()) {
            throw new StoreHandler(StoreErrorStatus.STORE_ALREADY_EXISTS);
        }
    }

    /**
     * 연락처 중복 검증
     */
    private void validateContactNumberNotExists(String contactNumber) {
        if (storeRepository.findByContactNumber(contactNumber).isPresent()) {
            throw new StoreHandler(StoreErrorStatus.STORE_ALREADY_EXISTS);
        }
    }

    /**
     * 좌표 유효성 검증
     */
    private void validateCoordinates(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            throw new StoreHandler(StoreErrorStatus.INVALID_COORDINATES);
        }
        
        if (latitude < -90.0 || latitude > 90.0) {
            throw new StoreHandler(StoreErrorStatus.INVALID_LATITUDE);
        }
        
        if (longitude < -180.0 || longitude > 180.0) {
            throw new StoreHandler(StoreErrorStatus.INVALID_LONGITUDE);
        }
    }

    /**
     * 메뉴 정보 검증
     */
    private void validateMenuInfo(String menuName, BigDecimal price) {
        if (menuName == null || menuName.trim().isEmpty()) {
            throw new StoreHandler(ErrorStatus._BAD_REQUEST);
        }
        
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new StoreHandler(StoreErrorStatus.INVALID_MENU_PRICE);
        }
    }

    /**
     * 엑셀 데이터로부터 Store 생성
     */
    private Store createStoreFromExcelData(StoreExcelData data) {
        try {
            BusinessType businessType = BusinessType.fromString(data.getBusinessType());
            
            Address address = new Address(
                data.getSido(),
                data.getSigun(), 
                data.getFullAddress(),
                data.getLatitude(),
                data.getLongitude()
            );

            Store store = Store.builder()
                .storeName(data.getStoreName())
                .businessType(businessType)
                .contactNumber(data.getContactNumber())
                .address(address)
                .isActive(true)
                .build();

            // 메뉴 추가
            addMenusFromExcelData(store, data);

            return storeRepository.save(store);
            
        } catch (Exception e) {
            throw new StoreHandler(ErrorStatus._BAD_REQUEST);
        }
    }

    /**
     * 엑셀 데이터에서 메뉴 정보 추가
     */
    private void addMenusFromExcelData(Store store, StoreExcelData data) {
        if (data.getMenu1() != null && data.getPrice1() != null) {
            store.addMenu(data.getMenu1(), data.getPrice1());
        }
        if (data.getMenu2() != null && data.getPrice2() != null) {
            store.addMenu(data.getMenu2(), data.getPrice2());
        }
        if (data.getMenu3() != null && data.getPrice3() != null) {
            store.addMenu(data.getMenu3(), data.getPrice3());
        }
        if (data.getMenu4() != null && data.getPrice4() != null) {
            store.addMenu(data.getMenu4(), data.getPrice4());
        }
    }

    /**
     * 엑셀 데이터 DTO (내부 클래스)
     */
    public static class StoreExcelData {
        private String sido;
        private String sigun;
        private String businessType;
        private String storeName;
        private String contactNumber;
        private String fullAddress;
        private String menu1;
        private BigDecimal price1;
        private String menu2;
        private BigDecimal price2;
        private String menu3;
        private BigDecimal price3;
        private String menu4;
        private BigDecimal price4;
        private Double latitude;
        private Double longitude;

        // getters and setters
        public String getSido() { return sido; }
        public void setSido(String sido) { this.sido = sido; }
        
        public String getSigun() { return sigun; }
        public void setSigun(String sigun) { this.sigun = sigun; }
        
        public String getBusinessType() { return businessType; }
        public void setBusinessType(String businessType) { this.businessType = businessType; }
        
        public String getStoreName() { return storeName; }
        public void setStoreName(String storeName) { this.storeName = storeName; }
        
        public String getContactNumber() { return contactNumber; }
        public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
        
        public String getFullAddress() { return fullAddress; }
        public void setFullAddress(String fullAddress) { this.fullAddress = fullAddress; }
        
        public String getMenu1() { return menu1; }
        public void setMenu1(String menu1) { this.menu1 = menu1; }
        
        public BigDecimal getPrice1() { return price1; }
        public void setPrice1(BigDecimal price1) { this.price1 = price1; }
        
        public String getMenu2() { return menu2; }
        public void setMenu2(String menu2) { this.menu2 = menu2; }
        
        public BigDecimal getPrice2() { return price2; }
        public void setPrice2(BigDecimal price2) { this.price2 = price2; }
        
        public String getMenu3() { return menu3; }
        public void setMenu3(String menu3) { this.menu3 = menu3; }
        
        public BigDecimal getPrice3() { return price3; }
        public void setPrice3(BigDecimal price3) { this.price3 = price3; }
        
        public String getMenu4() { return menu4; }
        public void setMenu4(String menu4) { this.menu4 = menu4; }
        
        public BigDecimal getPrice4() { return price4; }
        public void setPrice4(BigDecimal price4) { this.price4 = price4; }
        
        public Double getLatitude() { return latitude; }
        public void setLatitude(Double latitude) { this.latitude = latitude; }
        
        public Double getLongitude() { return longitude; }
        public void setLongitude(Double longitude) { this.longitude = longitude; }
    }
}
