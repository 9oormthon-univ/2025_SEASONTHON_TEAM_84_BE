package com.example.demo.domain.Favorite.entity;

import com.example.demo.domain.auditing.entity.BaseTimeEntity;
import com.example.demo.domain.member.entity.Member;
import com.example.demo.domain.store.entity.Store;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@Table(name = "favorite",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"member_id", "store_id"})
        },
        indexes = {
                @Index(name = "idx_favorite_member_id", columnList = "member_id"),
                @Index(name = "idx_favorite_store_id", columnList = "store_id")
        }
        )
public class Favorite extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    public static Favorite create(Member member, Store store) {
        return Favorite.builder()
                .member(member)
                .store(store)
                .build();
    }

}
