package com.example.demo.application.store;

import com.example.demo.domain.review.adaptor.ReviewAdaptor;
import com.example.demo.domain.review.entity.Review;
import com.example.demo.domain.store.adaptor.StoreAdaptor;
import com.example.demo.domain.store.entity.Store;
import com.example.demo.domain.store.exception.StoreErrorStatus;
import com.example.demo.domain.store.exception.StoreHandler;
import com.example.demo.domain.store.validator.StoreValidator;
import com.example.demo.domain.store.util.DistanceUtils;
import com.example.demo.infrastructure.annotation.usecase.UseCase;
import com.example.demo.presentation.review.dto.ReviewResponse;
import com.example.demo.presentation.store.dto.StoreResponse;
import com.example.demo.presentation.store.dto.StoreRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * 사용자 현재 위치 기반 착한가격업소 조회 UseCase
 */
@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetNearbyStoresUseCase {

    private final StoreAdaptor storeAdaptor;
    private final ReviewAdaptor reviewAdaptor;

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
            // 반경 내 조회를 Adaptor로 위임 (DB 정렬: 거리순)
            Page<Store> page = storeAdaptor.queryStoresWithinRadius(
                userLatitude,
                userLongitude,
                radiusKm,
                PageRequest.of(0, limit)
            );
            nearbyStores = page.getContent();
        } else {
            // 반경 제한이 없으면 메모리에서 거리 정렬 후 상위 N개 선택
            List<Store> allStoresWithCoordinates = storeAdaptor.queryStoresWithCoordinates();
            nearbyStores = allStoresWithCoordinates.stream()
                .sorted(createDistanceComparator(userLatitude, userLongitude))
                .limit(limit)
                .toList();
        }
        
        // 스토어들의 리뷰 정보를 한 번에 조회
        List<Long> storeIds = nearbyStores.stream()
                .map(Store::getId)
                .toList();
        
        Map<Long, ReviewAdaptor.StoreRatingInfo> storeRatings = reviewAdaptor.queryStoreRatings(storeIds);
        
        // 상위 리뷰 정보를 포함한 리뷰 요약 정보 생성
        Map<Long, StoreResponse.ReviewSummary> reviewSummaries = storeIds.stream()
                .collect(Collectors.toMap(
                    storeId -> storeId,
                    storeId -> {
                        ReviewAdaptor.StoreRatingInfo ratingInfo = storeRatings.get(storeId);
                        List<Review> topReviews = reviewAdaptor.queryTopReviewsByStoreId(storeId, 3);
                        List<ReviewResponse.ReviewInfo> topReviewInfos = topReviews.stream()
                                .map(ReviewResponse.ReviewInfo::from)
                                .toList();
                        
                        return StoreResponse.ReviewSummary.from(
                                ratingInfo != null ? ratingInfo.getAverageRating() : 0.0,
                                ratingInfo != null ? ratingInfo.getReviewCount() : 0L,
                                topReviewInfos
                        );
                    }
                ));

        // Store 엔터티를 NearbyStore DTO로 변환 (거리 정보 및 리뷰 정보 포함)
        List<StoreResponse.NearbyStore> nearbyStoreDtos = nearbyStores.stream()
            .map(store -> StoreResponse.NearbyStore.from(
                store,
                distanceToStore(userLatitude, userLongitude, store),
                reviewSummaries.get(store.getId())
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
