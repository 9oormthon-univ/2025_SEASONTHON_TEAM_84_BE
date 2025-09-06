package com.example.demo.domain.review.service;

import com.example.demo.domain.review.adaptor.ReviewAdaptor;
import com.example.demo.domain.review.entity.Review;
import com.example.demo.domain.store.adaptor.StoreAdaptor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Review 도메인 서비스
 * 복잡한 비즈니스 로직이나 여러 도메인 간의 조율이 필요할 때 사용
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewAdaptor reviewAdaptor;
    private final StoreAdaptor storeAdaptor;

    /**
     * 여러 업소의 리뷰 통계를 효율적으로 조회
     */
    public Map<Long, ReviewAdaptor.StoreRatingInfo> getStoreRatings(List<Long> storeIds) {
        return reviewAdaptor.queryStoreRatings(storeIds);
    }

    /**
     * 특정 업소의 상위 리뷰 조회
     */
    public List<Review> getTopReviews(Long storeId, int limit) {
        // 스토어 존재 확인
        storeAdaptor.queryById(storeId);
        return reviewAdaptor.queryTopReviewsByStoreId(storeId, limit);
    }

    /**
     * 특정 업소의 평균 평점 조회
     */
    public Double getAverageRating(Long storeId) {
        return reviewAdaptor.queryAverageRatingByStoreId(storeId).orElse(0.0);
    }

    /**
     * 특정 업소의 총 리뷰 수 조회
     */
    public long getReviewCount(Long storeId) {
        return reviewAdaptor.countReviewsByStoreId(storeId);
    }
}
