package com.example.demo.application.review;

import com.example.demo.domain.review.adaptor.ReviewAdaptor;
import com.example.demo.domain.review.entity.Review;
import com.example.demo.domain.store.adaptor.StoreAdaptor;
import com.example.demo.infrastructure.annotation.usecase.UseCase;
import com.example.demo.presentation.review.dto.ReviewRequest;
import com.example.demo.presentation.review.dto.ReviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetStoreReviewsUseCase {

    private final ReviewAdaptor reviewAdaptor;
    private final StoreAdaptor storeAdaptor;

    public ReviewResponse.ReviewList execute(Long storeId, ReviewRequest.GetReviews request) {
        // 스토어 존재 확인
        storeAdaptor.queryById(storeId);
        
        PageRequest pageRequest = PageRequest.of(request.getPage(), request.getSize());
        Page<Review> reviewPage = reviewAdaptor.queryReviewsByStoreId(storeId, pageRequest);
        
        return ReviewResponse.ReviewList.from(reviewPage);
    }

    public ReviewResponse.StoreRating executeStoreRating(Long storeId, int topReviewLimit) {
        // 스토어 존재 확인
        storeAdaptor.queryById(storeId);
        
        // 평균 평점 조회
        Double averageRating = reviewAdaptor.queryAverageRatingByStoreId(storeId).orElse(0.0);
        
        // 총 리뷰 수 조회
        long reviewCount = reviewAdaptor.countReviewsByStoreId(storeId);
        
        // 상위 리뷰 조회
        List<Review> topReviews = reviewAdaptor.queryTopReviewsByStoreId(storeId, topReviewLimit);
        
        return ReviewResponse.StoreRating.from(storeId, averageRating, reviewCount, topReviews);
    }
}
