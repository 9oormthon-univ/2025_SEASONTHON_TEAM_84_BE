package com.example.demo.domain.Favorite.repository;

import com.example.demo.domain.Favorite.entity.Favorite;
import com.example.demo.domain.member.entity.Member;
import com.example.demo.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    Optional<Favorite> findByMemberIdAndStoreId(Long memberId, Long storeId);

    List<Favorite> findByMemberId(Long memberId);

    boolean existsByMemberIdAndStoreId(Long memberId, Long storeId);

    void deleteByMemberIdAndStoreId(Long memberId, Long storeId);

    long countByStoreId(Long storeId);
}
