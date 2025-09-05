package com.example.demo.domain.store.service;


import com.example.demo.domain.store.entity.Address;
import com.example.demo.domain.store.entity.BusinessType;
import com.example.demo.domain.store.entity.Store;
import com.example.demo.domain.store.entity.StoreMenu;
import com.example.demo.domain.store.repository.StoreMenuRepository;
import com.example.demo.domain.store.repository.StoreRepository;
import com.example.demo.domain.store.exception.StoreHandler;
import com.example.demo.domain.store.exception.StoreErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreExcelService {

    private final StoreRepository storeRepository;
    private final StoreMenuRepository storeMenuRepository;
    private final int BATCH_SIZE = 1000;

    public void importStoresFromFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new StoreHandler(StoreErrorStatus.INVALID_FILE_FORMAT);
        }

        try {
            if (fileName.toLowerCase().endsWith(".csv")) {
                importFromCsv(file);
            } else if (fileName.toLowerCase().endsWith(".xlsx") || fileName.toLowerCase().endsWith(".xls")) {
                importFromExcel(file);
            } else {
                throw new StoreHandler(StoreErrorStatus.INVALID_FILE_FORMAT);
            }
        } catch (IOException e) {
            log.error("파일 읽기 중 오류 발생: {}", e.getMessage(), e);
            throw new StoreHandler(StoreErrorStatus.FILE_PROCESSING_ERROR);
        }
    }

    private void importFromCsv(MultipartFile file) throws IOException {
        List<Store> storeBatch = new ArrayList<>();
        List<StoreMenu> menuBatch = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"))) {
            String line;
            boolean isHeader = true;
            int processedCount = 0;
            
            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                
                if (line.trim().isEmpty()) continue;
                
                try {
                    String[] columns = parseCsvLine(line);
                    if (columns.length < 16) {
                        log.warn("컬럼 수가 부족한 행 건너뜀: {}", line);
                        continue;
                    }
                    
                    Store store = csvToStore(columns);
                    if (store != null) {
                        storeBatch.add(store);
                        List<StoreMenu> menus = csvToStoreMenus(columns, store);
                        menuBatch.addAll(menus);
                    }
                    
                    processedCount++;
                    if (processedCount % BATCH_SIZE == 0) {
                        saveBatch(storeBatch, menuBatch);
                        storeBatch.clear();
                        menuBatch.clear();
                        log.info("{}개 데이터 처리 완료", processedCount);
                    }
                } catch (Exception e) {
                    log.warn("행 처리 중 오류 발생, 건너뜀: {} - {}", line, e.getMessage());
                }
            }
            
            // 남은 데이터 저장
            if (!storeBatch.isEmpty()) {
                saveBatch(storeBatch, menuBatch);
                log.info("최종 {}개 데이터 처리 완료", processedCount);
            }
        }
    }

    private void importFromExcel(MultipartFile file) throws IOException {
        List<Store> storeBatch = new ArrayList<>();
        List<StoreMenu> menuBatch = new ArrayList<>();
        
        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            int processedCount = 0;

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (row.getRowNum() == 0 || isRowEmpty(row)) continue;

                try {
                    Store store = rowToStore(row);
                    if (store != null) {
                        storeBatch.add(store);
                        List<StoreMenu> menus = rowToStoreMenus(row, store);
                        menuBatch.addAll(menus);
                    }
                    
                    processedCount++;
                    if (processedCount % BATCH_SIZE == 0) {
                        saveBatch(storeBatch, menuBatch);
                        storeBatch.clear();
                        menuBatch.clear();
                        log.info("{}개 데이터 처리 완료", processedCount);
                    }
                } catch (Exception e) {
                    log.warn("행 {} 처리 중 오류 발생, 건너뜀: {}", row.getRowNum(), e.getMessage());
                }
            }
            
            // 남은 데이터 저장
            if (!storeBatch.isEmpty()) {
                saveBatch(storeBatch, menuBatch);
                log.info("최종 {}개 데이터 처리 완료", processedCount);
            }
        }
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) return true; //row 자체가 null이면 빈 행으로 처리

        for (Cell cell : row) {
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }


    @Transactional
    private void saveBatch(List<Store> stores, List<StoreMenu> menus) {
        if (stores.isEmpty()) return;
        
        try {
            // Store 먼저 저장
            List<Store> savedStores = storeRepository.saveAll(stores);
            
            // StoreMenu에 저장된 Store ID 설정
            Map<String, Store> storeMap = savedStores.stream()
                .collect(Collectors.toMap(
                    store -> generateStoreKey(store),
                    store -> store
                ));
            
            List<StoreMenu> menusWithStoreId = menus.stream()
                .map(menu -> {
                    String key = generateStoreKey(menu.getStore());
                    Store savedStore = storeMap.get(key);
                    if (savedStore != null) {
                        return StoreMenu.builder()
                            .store(savedStore)
                            .menuName(menu.getMenuName())
                            .price(menu.getPrice())
                            .menuOrder(menu.getMenuOrder())
                            .build();
                    }
                    return menu;
                })
                .collect(Collectors.toList());
            
            // StoreMenu 저장
            if (!menusWithStoreId.isEmpty()) {
                storeMenuRepository.saveAll(menusWithStoreId);
            }
            
        } catch (Exception e) {
            log.error("배치 저장 중 오류 발생: {}", e.getMessage(), e);
            throw new StoreHandler(StoreErrorStatus.DATABASE_ERROR);
        }
    }
    
    private String generateStoreKey(Store store) {
        return store.getStoreName() + "_" + 
               (store.getAddress() != null ? store.getAddress().getFullAddress() : "") + "_" +
               (store.getContactNumber() != null ? store.getContactNumber() : "");
    }

    private Store rowToStore(Row row) {
        try {
            String sido = getCellValueAsString(row.getCell(0));
            String sigun = getCellValueAsString(row.getCell(1));
            String businessTypeStr = getCellValueAsString(row.getCell(2));
            String storeName = getCellValueAsString(row.getCell(3));
            String contactNumber = getCellValueAsString(row.getCell(4));
            String fullAddress = getCellValueAsString(row.getCell(5));
            Double longitude = getCellValueAsDouble(row.getCell(14));
            Double latitude = getCellValueAsDouble(row.getCell(15));
            
            if (storeName == null || storeName.trim().isEmpty()) {
                return null;
            }
            
            BusinessType businessType = BusinessType.fromString(businessTypeStr);
            Address address = new Address(sido, sigun, fullAddress, latitude, longitude);
            
            return Store.create(storeName, businessType, contactNumber, address);
            
        } catch (Exception e) {
            log.warn("Store 변환 중 오류: {}", e.getMessage());
            return null;
        }
    }

    private List<StoreMenu> rowToStoreMenus(Row row, Store store) {
        List<StoreMenu> menus = new ArrayList<>();
        
        try {
            // 메뉴1~4 처리 (컬럼 6,7 / 8,9 / 10,11 / 12,13)
            for (int i = 0; i < 4; i++) {
                int menuNameIndex = 6 + (i * 2);
                int priceIndex = 7 + (i * 2);
                
                String menuName = getCellValueAsString(row.getCell(menuNameIndex));
                BigDecimal price = getCellValueAsBigDecimal(row.getCell(priceIndex));
                
                if (menuName != null && !menuName.trim().isEmpty() && 
                    price != null && price.compareTo(BigDecimal.ZERO) > 0) {
                    
                    StoreMenu menu = StoreMenu.builder()
                        .store(store)
                        .menuName(menuName.trim())
                        .price(price)
                        .menuOrder(i + 1)
                        .build();
                    menus.add(menu);
                }
            }
        } catch (Exception e) {
            log.warn("StoreMenu 변환 중 오류: {}", e.getMessage());
        }
        
        return menus;
    }
    
    private Store csvToStore(String[] columns) {
        try {
            String sido = columns[0].trim();
            String sigun = columns[1].trim();
            String businessTypeStr = columns[2].trim();
            String storeName = columns[3].trim();
            String contactNumber = columns[4].trim();
            String fullAddress = columns[5].trim();
            Double longitude = parseDouble(columns[14]);
            Double latitude = parseDouble(columns[15]);
            
            if (storeName.isEmpty()) {
                return null;
            }
            
            BusinessType businessType = BusinessType.fromString(businessTypeStr);
            Address address = new Address(sido, sigun, fullAddress, latitude, longitude);
            
            return Store.create(storeName, businessType, 
                contactNumber.isEmpty() ? null : contactNumber, address);
            
        } catch (Exception e) {
            log.warn("CSV Store 변환 중 오류: {}", e.getMessage());
            return null;
        }
    }
    
    private List<StoreMenu> csvToStoreMenus(String[] columns, Store store) {
        List<StoreMenu> menus = new ArrayList<>();
        
        try {
            // 메뉴1~4 처리 (컬럼 6,7 / 8,9 / 10,11 / 12,13)
            for (int i = 0; i < 4; i++) {
                int menuNameIndex = 6 + (i * 2);
                int priceIndex = 7 + (i * 2);
                
                if (menuNameIndex < columns.length && priceIndex < columns.length) {
                    String menuName = columns[menuNameIndex].trim();
                    BigDecimal price = parseBigDecimal(columns[priceIndex]);
                    
                    if (!menuName.isEmpty() && price != null && price.compareTo(BigDecimal.ZERO) > 0) {
                        StoreMenu menu = StoreMenu.builder()
                            .store(store)
                            .menuName(menuName)
                            .price(price)
                            .menuOrder(i + 1)
                            .build();
                        menus.add(menu);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("CSV StoreMenu 변환 중 오류: {}", e.getMessage());
        }
        
        return menus;
    }
    
    private String[] parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder currentField = new StringBuilder();
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(currentField.toString());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }
        result.add(currentField.toString());
        
        return result.toArray(new String[0]);
    }
    
    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }
    
    private Double getCellValueAsDouble(Cell cell) {
        if (cell == null) return null;
        
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return cell.getNumericCellValue();
            } else if (cell.getCellType() == CellType.STRING) {
                return Double.parseDouble(cell.getStringCellValue());
            }
        } catch (NumberFormatException e) {
            log.warn("숫자 변환 실패: {}", cell.toString());
        }
        return null;
    }
    
    private BigDecimal getCellValueAsBigDecimal(Cell cell) {
        if (cell == null) return null;
        
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return BigDecimal.valueOf(cell.getNumericCellValue());
            } else if (cell.getCellType() == CellType.STRING) {
                String value = cell.getStringCellValue().replaceAll("[^0-9.]", "");
                if (!value.isEmpty()) {
                    return new BigDecimal(value);
                }
            }
        } catch (NumberFormatException e) {
            log.warn("BigDecimal 변환 실패: {}", cell.toString());
        }
        return null;
    }
    
    private Double parseDouble(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            String cleanValue = value.trim().replaceAll("[^0-9.]", "");
            if (!cleanValue.isEmpty()) {
                return new BigDecimal(cleanValue);
            }
        } catch (NumberFormatException e) {
            // ignore
        }
        return null;
    }


}