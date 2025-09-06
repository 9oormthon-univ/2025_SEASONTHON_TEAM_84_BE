package com.example.demo.domain.review.repository;

import com.example.demo.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * 특정 스토어의 활성화된 리뷰 조회 (페이징)
     */
    Page<Review> findByStoreIdAndIsActiveTrueOrderByCreatedDateDesc(Long storeId, Pageable pageable);

    /**
     * 특정 스토어의 활성화된 리뷰 전체 조회
     */
    List<Review> findByStoreIdAndIsActiveTrueOrderByCreatedDateDesc(Long storeId);

    /**
     * 특정 스토어의 상위 N개 리뷰 조회 (최신순)
     */
    @Query("SELECT r FROM Review r WHERE r.store.id = :storeId AND r.isActive = true ORDER BY r.createdDate DESC")
    List<Review> findTopReviewsByStoreId(@Param("storeId") Long storeId, Pageable pageable);

    /**
     * 특정 스토어의 평균 평점 조회
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.store.id = :storeId AND r.isActive = true")
    Optional<Double> findAverageRatingByStoreId(@Param("storeId") Long storeId);

    /**
     * 특정 스토어의 총 리뷰 수 조회
     */
    long countByStoreIdAndIsActiveTrue(Long storeId);

    /**
     * 사용자가 특정 스토어에 작성한 리뷰 조회
     */
    Optional<Review> findByStoreIdAndMemberIdAndIsActiveTrue(Long storeId, Long memberId);

    /**
     * 사용자가 작성한 모든 활성화된 리뷰 조회
     */
    Page<Review> findByMemberIdAndIsActiveTrueOrderByCreatedDateDesc(Long memberId, Pageable pageable);

    /**
     * 여러 스토어의 평균 평점을 한 번에 조회
     */
    @Query("SELECT r.store.id as storeId, AVG(r.rating) as avgRating, COUNT(r) as reviewCount " +
           "FROM Review r WHERE r.store.id IN :storeIds AND r.isActive = true GROUP BY r.store.id")
    List<StoreRatingProjection> findStoreRatings(@Param("storeIds") List<Long> storeIds);

    /**
     * 스토어 평점 정보를 위한 Projection 인터페이스
     */
    interface StoreRatingProjection {
        Long getStoreId();
        Double getAvgRating();
        Long getReviewCount();
    }
}
