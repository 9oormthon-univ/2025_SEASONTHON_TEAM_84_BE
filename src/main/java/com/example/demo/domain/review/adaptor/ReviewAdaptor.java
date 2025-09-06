package com.example.demo.domain.review.adaptor;

import com.example.demo.domain.review.entity.Review;
import com.example.demo.infrastructure.exception.object.general.GeneralException;
import com.example.demo.infrastructure.exception.payload.code.ErrorStatus;
import com.example.demo.domain.review.repository.ReviewRepository;
import com.example.demo.infrastructure.annotation.adaptor.Adaptor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Adaptor
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewAdaptor {

    private final ReviewRepository reviewRepository;

    public Review queryById(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._BAD_REQUEST));
    }

    @Transactional
    public Review save(Review review) {
        return reviewRepository.save(review);
    }

    @Transactional
    public void delete(Review review) {
        review.deactivate();
        reviewRepository.save(review);
    }

    public Page<Review> queryReviewsByStoreId(Long storeId, Pageable pageable) {
        return reviewRepository.findByStoreIdAndIsActiveTrueOrderByCreatedDateDesc(storeId, pageable);
    }

    public List<Review> queryTopReviewsByStoreId(Long storeId, int limit) {
        return reviewRepository.findTopReviewsByStoreId(storeId, PageRequest.of(0, limit));
    }

    public Optional<Double> queryAverageRatingByStoreId(Long storeId) {
        return reviewRepository.findAverageRatingByStoreId(storeId);
    }

    public long countReviewsByStoreId(Long storeId) {
        return reviewRepository.countByStoreIdAndIsActiveTrue(storeId);
    }

    public Optional<Review> queryReviewByStoreAndMember(Long storeId, Long memberId) {
        return reviewRepository.findByStoreIdAndMemberIdAndIsActiveTrue(storeId, memberId);
    }

    public Page<Review> queryReviewsByMemberId(Long memberId, Pageable pageable) {
        return reviewRepository.findByMemberIdAndIsActiveTrueOrderByCreatedDateDesc(memberId, pageable);
    }

    /**
     * 여러 스토어의 평점 정보를 한 번에 조회
     */
    public Map<Long, StoreRatingInfo> queryStoreRatings(List<Long> storeIds) {
        List<ReviewRepository.StoreRatingProjection> projections = 
            reviewRepository.findStoreRatings(storeIds);
        
        return projections.stream()
                .collect(Collectors.toMap(
                    ReviewRepository.StoreRatingProjection::getStoreId,
                    projection -> StoreRatingInfo.builder()
                            .averageRating(projection.getAvgRating() != null ? projection.getAvgRating() : 0.0)
                            .reviewCount(projection.getReviewCount())
                            .build()
                ));
    }

    /**
     * 스토어 평점 정보 클래스
     */
    @lombok.Builder
    @lombok.Getter
    public static class StoreRatingInfo {
        private final Double averageRating;
        private final Long reviewCount;
    }
}
