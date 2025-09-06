package com.example.demo.application.store;

import com.example.demo.domain.review.adaptor.ReviewAdaptor;
import com.example.demo.domain.review.entity.Review;
import com.example.demo.domain.store.adaptor.StoreAdaptor;
import com.example.demo.domain.store.entity.Store;
import com.example.demo.infrastructure.annotation.usecase.UseCase;
import com.example.demo.presentation.review.dto.ReviewResponse;
import com.example.demo.presentation.store.dto.StoreResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetStoreDetailUseCase {

    private final StoreAdaptor storeAdaptor;
    private final ReviewAdaptor reviewAdaptor;

    public StoreResponse.StoreDetailWithReviews execute(Long storeId) {
        // 업소 정보 조회 (메뉴 포함)
        Store store = storeAdaptor.queryByIdFetchMenu(storeId);
        
        // 리뷰 통계 정보 조회
        Double averageRating = reviewAdaptor.queryAverageRatingByStoreId(storeId).orElse(0.0);
        long reviewCount = reviewAdaptor.countReviewsByStoreId(storeId);
        
        // 상위 리뷰 조회 (최신 5개)
        List<Review> topReviews = reviewAdaptor.queryTopReviewsByStoreId(storeId, 5);
        List<ReviewResponse.ReviewInfo> topReviewInfos = topReviews.stream()
                .map(ReviewResponse.ReviewInfo::from)
                .toList();

        // 리뷰 요약 정보 생성
        StoreResponse.ReviewSummary reviewSummary = StoreResponse.ReviewSummary.from(
                averageRating, reviewCount, topReviewInfos
        );

        return StoreResponse.StoreDetailWithReviews.from(store, reviewSummary);
    }

}
