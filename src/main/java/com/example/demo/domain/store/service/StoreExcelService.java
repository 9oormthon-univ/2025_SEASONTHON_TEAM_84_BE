package com.example.demo.domain.store.service;


import com.example.demo.domain.store.entity.Store;
import com.example.demo.domain.store.entity.StoreMenu;
import com.example.demo.domain.store.repository.StoreMenuRepository;
import com.example.demo.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class StoreExcelService {

    private final StoreRepository storeRepository;
    private final StoreMenuRepository storeMenuRepository;
    private final int BATCH_SIZE = 1000;

    public void importStoresFromFile(MultipartFile file) {
        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0); // 첫 번째 시트
            Iterator<Row> rowIterator = sheet.iterator(); // 반복 객체 생성

            Map<Store, List<StoreMenu>> storeListMap = new HashMap<>();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (row.getRowNum() == 0 || isRowEmpty(row)) continue; // 첫 번째 줄은 헤더이므로 건너뜀

                Store store = rowToStore(row);
                List<StoreMenu> menus = rowToStoreMenus(row, store);
                storeListMap.putIfAbsent(store, menus);
                if (isBatchSizeReached(storeListMap)) {
                    //todo 여기에 지금까지 쌓인 Store과 StoreMenus 저장
                    storeListMap.clear();
                }

            }
            //나머지 저장
        } catch (IOException e) {
            throw new RuntimeException(e); //커스텀 에러 StoreHandler(StoreErrorStatus)
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


    private boolean isBatchSizeReached(Map<Store, List<StoreMenu>> storeListMap) {
        return storeListMap.size() >= BATCH_SIZE;
    }

    private Store saveStore(Store store) {
        return storeRepository.save(store);
    }

    private void saveStoreMenus(List<StoreMenu> menus) {
        storeMenuRepository.saveAll(menus);
    }

    private Store rowToStore(Row row) {
        return null;
    }

    private List<StoreMenu> rowToStoreMenus(Row row, Store store) {
        return null;
    }


}