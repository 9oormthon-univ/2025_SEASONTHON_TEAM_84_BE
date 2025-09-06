package com.example.demo.application.review;

import com.example.demo.domain.member.entity.Member;
import com.example.demo.domain.review.adaptor.ReviewAdaptor;
import com.example.demo.domain.review.entity.Review;
import com.example.demo.infrastructure.exception.object.general.GeneralException;
import com.example.demo.infrastructure.exception.payload.code.ErrorStatus;
import com.example.demo.infrastructure.annotation.usecase.UseCase;
import com.example.demo.presentation.review.dto.ReviewRequest;
import com.example.demo.presentation.review.dto.ReviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class UpdateReviewUseCase {

    private final ReviewAdaptor reviewAdaptor;

    public ReviewResponse.ReviewInfo execute(Member currentMember, Long reviewId, ReviewRequest.UpdateReview request) {
        Review review = reviewAdaptor.queryById(reviewId);
        
        validateOwnership(review, currentMember.getId());
        validateRequest(request);
        
        review.updateReview(request.getRating(), request.getContent());
        Review updatedReview = reviewAdaptor.save(review);
        
        return ReviewResponse.ReviewInfo.from(updatedReview);
    }

    private void validateOwnership(Review review, Long currentMemberId) {
        if (!review.isOwner(currentMemberId)) {
            throw new GeneralException(ErrorStatus._FORBIDDEN);
        }
    }

    private void validateRequest(ReviewRequest.UpdateReview request) {
        if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            throw new GeneralException(ErrorStatus._BAD_REQUEST);
        }
        
        if (request.getContent() != null && request.getContent().length() > 1000) {
            throw new GeneralException(ErrorStatus._BAD_REQUEST);
        }
    }
}
