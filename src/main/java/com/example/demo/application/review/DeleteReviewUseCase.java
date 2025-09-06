package com.example.demo.application.review;

import com.example.demo.domain.member.entity.Member;
import com.example.demo.domain.review.adaptor.ReviewAdaptor;
import com.example.demo.domain.review.entity.Review;
import com.example.demo.infrastructure.exception.object.general.GeneralException;
import com.example.demo.infrastructure.exception.payload.code.ErrorStatus;
import com.example.demo.infrastructure.annotation.usecase.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class DeleteReviewUseCase {

    private final ReviewAdaptor reviewAdaptor;

    public void execute(Member currentMember, Long reviewId) {
        Review review = reviewAdaptor.queryById(reviewId);
        
        validateOwnership(review, currentMember.getId());
        
        reviewAdaptor.delete(review);
    }

    private void validateOwnership(Review review, Long currentMemberId) {
        if (!review.isOwner(currentMemberId)) {
            throw new GeneralException(ErrorStatus._FORBIDDEN);
        }
    }
}
