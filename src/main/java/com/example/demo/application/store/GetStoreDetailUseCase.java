package com.example.demo.application.store;

import com.example.demo.domain.store.adaptor.StoreAdaptor;
import com.example.demo.domain.store.entity.Store;
import com.example.demo.infrastructure.annotation.usecase.UseCase;
import com.example.demo.presentation.store.dto.StoreResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
@RequiredArgsConstructor
public class GetStoreDetailUseCase {

    private final StoreAdaptor storeAdaptor;

    public StoreResponse.StoreDetail execute(Long storeId) {
        Store store = storeAdaptor.queryByIdFetchMenu(storeId);
        return StoreResponse.StoreDetail.from(store);
    }

}
