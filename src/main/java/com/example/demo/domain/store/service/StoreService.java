package com.example.demo.domain.store.service;

import com.example.demo.domain.store.entity.Address;
import com.example.demo.domain.store.entity.BusinessType;
import com.example.demo.domain.store.entity.Store;
import com.example.demo.domain.store.exception.StoreErrorStatus;
import com.example.demo.domain.store.exception.StoreHandler;
import com.example.demo.domain.store.adaptor.StoreAdaptor;
import com.example.demo.domain.store.repository.StoreRepository;
import com.example.demo.domain.store.validator.StoreValidator;
import com.example.demo.infrastructure.exception.payload.code.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StoreService {

    private final StoreRepository storeRepository;
    private final StoreAdaptor storeAdaptor;

    public Store createStore(String storeName, BusinessType businessType, String contactNumber,
                           String sido, String sigun, String fullAddress, 
                           Double latitude, Double longitude) {
        storeAdaptor.validateStoreNameNotExists(storeName);
        if (contactNumber != null && !contactNumber.trim().isEmpty()) {
            storeAdaptor.validateContactNumberNotExists(contactNumber);
        }
        Address address = new Address(sido, sigun, fullAddress, latitude, longitude);
        validateAddress(address);
        Store store = Store.create(storeName, businessType, contactNumber, address);
        return storeRepository.save(store);
    }

    public Store updateStore(Long storeId, String storeName, BusinessType businessType, 
                           String contactNumber) {
        Store store = findStoreById(storeId);
        if (storeName != null && !storeName.equals(store.getStoreName())) {
            storeAdaptor.validateStoreNameNotExists(storeName);
        }
        if (contactNumber != null && !contactNumber.equals(store.getContactNumber())) {
            storeAdaptor.validateContactNumberNotExists(contactNumber);
        }

        Store updatedStore = store.updateStoreInfo(storeName, businessType, contactNumber);
        return storeRepository.save(updatedStore);
    }

    public Store updateStoreCoordinates(Long storeId, Double latitude, Double longitude) {
        Store store = findStoreById(storeId);
        StoreValidator.validateCoordinates(latitude, longitude);
        store.updateCoordinates(latitude, longitude);
        return storeRepository.save(store);
    }

    public Store addMenu(Long storeId, String menuName, BigDecimal price) {
        Store store = findStoreById(storeId);
        StoreValidator.validateMenuInfo(menuName, price);
        store.addMenu(menuName, price);
        return storeRepository.save(store);
    }

    public Store toggleStoreStatus(Long storeId) {
        Store store = findStoreById(storeId);
        store.toggleActiveStatus();
        return storeRepository.save(store);
    }

    public void deleteStore(Long storeId) {
        Store store = findStoreById(storeId);
        if (store.isActive()) {
            store.toggleActiveStatus();
            storeRepository.save(store);
        }
    }

    public void permanentDeleteStore(Long storeId) {
        Store store = findStoreById(storeId);
        storeRepository.delete(store);
    }

    public List<Store> bulkCreateStores(List<StoreExcelData> excelDataList) {
        return excelDataList.stream()
            .map(this::createStoreFromExcelData)
            .toList();
    }

    private Store findStoreById(Long storeId) {
        return storeRepository.findById(storeId)
            .orElseThrow(() -> new StoreHandler(StoreErrorStatus.STORE_NOT_FOUND));
    }

    private void validateAddress(Address address) {
        if (!address.isValidAddress()) {
            throw new StoreHandler(StoreErrorStatus.INVALID_COORDINATES);
        }
    }

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

            Store store = Store.create(
                data.getStoreName(),
                businessType,
                data.getContactNumber(),
                address
            );

            addMenusFromExcelData(store, data);

            return storeRepository.save(store);
            
        } catch (Exception e) {
            throw new StoreHandler(ErrorStatus._BAD_REQUEST);
        }
    }

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
