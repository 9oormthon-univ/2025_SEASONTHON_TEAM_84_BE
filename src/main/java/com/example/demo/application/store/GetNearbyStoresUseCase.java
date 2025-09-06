package com.example.demo.application.store;

import com.example.demo.domain.store.adaptor.StoreAdaptor;
import com.example.demo.domain.store.entity.Store;
import com.example.demo.domain.store.exception.StoreErrorStatus;
import com.example.demo.domain.store.exception.StoreHandler;
import com.example.demo.domain.store.validator.StoreValidator;
import com.example.demo.domain.store.util.DistanceUtils;
import com.example.demo.infrastructure.annotation.usecase.UseCase;
import com.example.demo.presentation.store.dto.StoreResponse;
import com.example.demo.presentation.store.dto.StoreRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

/**
 * 사용자 현재 위치 기반 착한가격업소 조회 UseCase
 */
@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetNearbyStoresUseCase {

    private final StoreAdaptor storeAdaptor;

    /**
     * 사용자 현재 위치 기반 가까운 착한가격업소 조회
     * @return 거리 정보가 포함된 주변 업소 목록 응답
     */
    public StoreResponse.NearbyStoreList execute(StoreRequest.GetNearbyStores request) {
        Double userLatitude = request.getLatitude();
        Double userLongitude = request.getLongitude();
        Integer limit = request.getLimit();
        Double radiusKm = request.getRadiusKm();

        // 입력값 검증
        validateInputParameters(userLatitude, userLongitude, limit, radiusKm);
        
        List<Store> nearbyStores;

        if (radiusKm != null) {
            // 반경 내 조회를 Adaptor로 위임 (DB 정렬: 거리순) + 메뉴 fetch join 보장
            nearbyStores = storeAdaptor.queryStoresWithinRadiusWithMenus(
                userLatitude,
                userLongitude,
                radiusKm,
                limit
            );
        } else {
            // 반경 제한이 없으면 좌표 보유 업소를 메뉴까지 fetch join 후 메모리에서 거리 정렬
            List<Store> allStoresWithCoordinates = storeAdaptor.queryStoresWithCoordinatesFetchMenus();
            nearbyStores = allStoresWithCoordinates.stream()
                .sorted(createDistanceComparator(userLatitude, userLongitude))
                .limit(limit)
                .toList();
        }
        
        // Store 엔터티를 NearbyStore DTO로 변환 (거리 정보 포함)
        List<StoreResponse.NearbyStore> nearbyStoreDtos = nearbyStores.stream()
            .map(store -> StoreResponse.NearbyStore.from(
                store,
                distanceToStore(userLatitude, userLongitude, store)
            ))
            .toList();

        // 최종 응답 객체 생성 및 반환
        return StoreResponse.NearbyStoreList.from(userLatitude, userLongitude, nearbyStoreDtos);
    }


    /**
     * 입력 파라미터 검증
     */
    private void validateInputParameters(Double latitude, Double longitude, Integer limit, Double radiusKm) {
        StoreValidator.validateCoordinates(latitude, longitude);

        if (limit == null || limit <= 0) {
            throw new StoreHandler(StoreErrorStatus.INVALID_LIMIT);
        }
        
        if (limit > 100) {
            throw new StoreHandler(StoreErrorStatus.INVALID_LIMIT);
        }

        if (radiusKm != null) {
            StoreValidator.validateRadius(radiusKm);
        }
    }

    /**
     * 거리 기준 정렬을 위한 Comparator 생성
     */
    private Comparator<Store> createDistanceComparator(Double userLatitude, Double userLongitude) {
        return (store1, store2) -> {
            double distance1 = distanceToStore(userLatitude, userLongitude, store1);
            double distance2 = distanceToStore(userLatitude, userLongitude, store2);
            return Double.compare(distance1, distance2);
        };
    }

    /**
     * 사용자 위치와 업소 간의 거리 계산
     */
    private double distanceToStore(Double userLatitude, Double userLongitude, Store store) {
        if (store.getAddress() == null || !store.getAddress().hasValidCoordinates()) {
            return Double.MAX_VALUE;
        }

        return DistanceUtils.calculateDistanceKm(
            userLatitude,
            userLongitude,
            store.getAddress().getLatitude(),
            store.getAddress().getLongitude()
        );
    }
}
