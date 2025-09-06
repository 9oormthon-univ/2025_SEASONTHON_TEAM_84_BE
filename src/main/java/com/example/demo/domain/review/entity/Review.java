package com.example.demo.domain.review.entity;

import com.example.demo.domain.auditing.entity.BaseTimeEntity;
import com.example.demo.domain.member.entity.Member;
import com.example.demo.domain.store.entity.Store;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "review",
        indexes = {
                @Index(name = "idx_review_store", columnList = "store_id"),
                @Index(name = "idx_review_member", columnList = "member_id"),
                @Index(name = "idx_review_rating", columnList = "rating"),
                @Index(name = "idx_review_created_date", columnList = "createdDate")
        }
)
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private Integer rating; // 1~5점

    @Column(length = 1000)
    private String content;

    @Column(name = "is_active")
    @Builder.Default
    private boolean isActive = true;

    // 비즈니스 로직
    public void updateReview(Integer rating, String content) {
        if (rating != null && rating >= 1 && rating <= 5) {
            this.rating = rating;
        }
        if (content != null) {
            this.content = content;
        }
    }

    public void deactivate() {
        this.isActive = false;
    }

    public boolean isOwner(Long memberId) {
        return this.member.getId().equals(memberId);
    }
}
