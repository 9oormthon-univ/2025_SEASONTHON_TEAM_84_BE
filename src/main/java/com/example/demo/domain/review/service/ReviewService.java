package com.example.demo.domain.review.service;

import com.example.demo.domain.review.entity.Review;
import com.example.demo.domain.review.exception.ReviewErrorStatus;
import com.example.demo.domain.review.exception.ReviewHandler;
import com.example.demo.domain.review.repository.ReviewRepository;
import com.example.demo.domain.store.entity.Store;
import com.example.demo.domain.store.exception.StoreErrorStatus;
import com.example.demo.domain.store.exception.StoreHandler;
import com.example.demo.domain.store.repository.StoreRepository;
import com.example.demo.presentation.store.dto.ReviewRankingDto;
import com.example.demo.presentation.store.dto.ReviewRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final StoreRepository storeRepository;

    //리뷰 작성
    public Review createReview(ReviewRequestDto request) {
        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new StoreHandler(StoreErrorStatus.STORE_NOT_FOUND));

        Review review = Review.builder()
                .content(request.getContent())
                .writerId(request.getWriterId())
                .rating(request.getRating())
                .store(store)
                .build();

        return reviewRepository.save(review);
    }

    // 리뷰 수정
    public Review updateReview(Long reviewId, String content, int rating) {
        Review review = reviewRepository.findByIdAndIsDeletedFalse(reviewId)
                .orElseThrow(() -> new ReviewHandler(ReviewErrorStatus.REVIEW_NOT_FOUND));

        review.updateReview(content, rating);
        return reviewRepository.save(review);
    }

    // 리뷰 삭제
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findByIdAndIsDeletedFalse(reviewId)
                .orElseThrow(() -> new ReviewHandler(ReviewErrorStatus.REVIEW_NOT_FOUND));

        review.softDelete();
        reviewRepository.save(review);
    }

    //특정 가게의 리뷰 조회
    @Transactional(readOnly = true)
    public List<Review> getReviewsByStore(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreHandler(StoreErrorStatus.STORE_NOT_FOUND));
        return reviewRepository.findByStoreAndIsDeletedFalse(store);
    }

    //가게별 별점 평균 조회
    @Transactional(readOnly = true)
    public double getAverageRating(Long storeId) {
        if (!storeRepository.existsById(storeId)) {
            throw new StoreHandler(StoreErrorStatus.STORE_NOT_FOUND);
        }
        List<Review> reviews = reviewRepository.findByStoreIdAndIsDeletedFalse(storeId);
        if (reviews.isEmpty()) return 0.0;

        double average = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        return Math.round(average * 10) / 10.0; // 소수점 1자리
    }

    //작성자별 리뷰 조회
    @Transactional(readOnly = true)
    public List<Review> getReviewsByWriter(Long writerId) {
        return reviewRepository.findByWriterIdAndIsDeletedFalse(writerId);
    }

    // 전체 업소 랭킹 (순위와 이름만)
    @Transactional(readOnly = true)
    public List<ReviewRankingDto> getAllStoreRanking() {

        List<Store> stores = storeRepository.findAll();

        List<ReviewRankingDto> rankings = stores.stream()
                .map(store -> {
                    List<Review> reviews = reviewRepository.findByStoreAndIsDeletedFalse(store);

                    if (reviews.isEmpty()) {
                        return null;
                    }

                    double average = reviews.stream()
                            .mapToInt(Review::getRating)
                            .average()
                            .orElse(0.0);

                    return ReviewRankingDto.builder()
                            .storeName(store.getStoreName())
                            .averageRating(Math.round(average * 10) / 10.0)   //소수점 첫째 자리까지
                            .build();
                })
                .filter(dto -> dto != null)
                .sorted((a, b) -> Double.compare(b.getAverageRating(), a.getAverageRating()))  //내림차순
                .toList();

        // 순위 부여
        for (int i = 0; i < rankings.size(); i++) {
            rankings.get(i).setRank(i + 1);
        }

        return rankings;
    }
}
