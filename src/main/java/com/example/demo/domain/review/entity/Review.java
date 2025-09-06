package com.example.demo.domain.review.entity;

import com.example.demo.domain.auditing.entity.BaseTimeEntity;
import com.example.demo.domain.store.entity.Store;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@Table(
        name = "review",
        indexes = {
                @Index(name = "idx_review_store_id", columnList = "store_id"),
                @Index(name = "idx_review_writer_id", columnList = "writer_id"),
        }
)
public class Review extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(nullable = false)
    private Long writerId;

    @Column(nullable = false)
    private int rating; // 1~5

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

    public void setRating(int rating) {
        if (rating < 1 || rating > 5) throw new IllegalArgumentException("별점은 1~5 사이여야 합니다.");
        this.rating = rating;
    }

    // 수정
    public void updateReview(String content, int rating) {
        this.content = content;
        setRating(rating);
    }

    //삭제
    public void softDelete() {
        this.isDeleted = true;
    }

}

