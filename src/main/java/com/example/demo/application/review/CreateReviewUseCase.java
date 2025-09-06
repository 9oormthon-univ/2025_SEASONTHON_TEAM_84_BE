package com.example.demo.application.review;

import com.example.demo.domain.member.entity.Member;
import com.example.demo.domain.review.adaptor.ReviewAdaptor;
import com.example.demo.domain.review.entity.Review;
import com.example.demo.infrastructure.exception.object.general.GeneralException;
import com.example.demo.infrastructure.exception.payload.code.ErrorStatus;
import com.example.demo.domain.store.adaptor.StoreAdaptor;
import com.example.demo.domain.store.entity.Store;
import com.example.demo.infrastructure.annotation.usecase.UseCase;
import com.example.demo.presentation.review.dto.ReviewRequest;
import com.example.demo.presentation.review.dto.ReviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class CreateReviewUseCase {

    private final ReviewAdaptor reviewAdaptor;
    private final StoreAdaptor storeAdaptor;

    public ReviewResponse.CreateReviewResponse execute(Member currentMember, ReviewRequest.CreateReview request) {
        validateRequest(request);
        
        Store store = storeAdaptor.queryById(request.getStoreId());
        
        // 중복 리뷰 체크
        reviewAdaptor.queryReviewByStoreAndMember(store.getId(), currentMember.getId())
                .ifPresent(existingReview -> {
                    throw new GeneralException(ErrorStatus._BAD_REQUEST);
                });
        
        Review review = Review.builder()
                .store(store)
                .member(currentMember)
                .rating(request.getRating())
                .content(request.getContent())
                .build();
        
        Review savedReview = reviewAdaptor.save(review);
        
        return ReviewResponse.CreateReviewResponse.from(savedReview);
    }

    private void validateRequest(ReviewRequest.CreateReview request) {
        if (request.getStoreId() == null) {
            throw new GeneralException(ErrorStatus._BAD_REQUEST);
        }
        
        if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            throw new GeneralException(ErrorStatus._BAD_REQUEST);
        }
        
        if (request.getContent() != null && request.getContent().length() > 1000) {
            throw new GeneralException(ErrorStatus._BAD_REQUEST);
        }
    }
}
