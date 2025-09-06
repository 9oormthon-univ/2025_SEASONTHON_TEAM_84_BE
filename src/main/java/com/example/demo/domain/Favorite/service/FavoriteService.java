package com.example.demo.domain.Favorite.service;

import com.example.demo.domain.Favorite.entity.Favorite;
import com.example.demo.domain.Favorite.repository.FavoriteRepository;
import com.example.demo.domain.member.adaptor.MemberAdaptor;
import com.example.demo.domain.member.entity.Member;
import com.example.demo.domain.member.repository.MemberRepository;
import com.example.demo.domain.store.entity.Store;
import com.example.demo.domain.store.repository.StoreRepository;
import com.example.demo.presentation.store.dto.FavoriteResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final StoreRepository storeRepository;
    private final MemberRepository memberRepository;

    // 즐겨찾기 추가
    public Favorite addFavorite(Long memberId, Long storeId) {
        // 이미 즐겨찾기한 업소인지 확인
        if (favoriteRepository.existsByMemberIdAndStoreId(memberId, storeId)) {
            throw new IllegalArgumentException("이미 즐겨찾기한 업소입니다.");
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("업소를 찾을 수 없습니다."));

        Favorite favorite = Favorite.create(member, store);
        return favoriteRepository.save(favorite);
    }

    // 즐겨찾기 삭제
    public void removeFavorite(Long memberId, Long storeId) {
        favoriteRepository.deleteByMemberIdAndStoreId(memberId, storeId);
    }

    // 즐겨찾기 토글 (있으면 삭제, 없으면 추가)
    public boolean toggleFavorite(Long memberId, Long storeId) {
        if (favoriteRepository.existsByMemberIdAndStoreId(memberId, storeId)) {
            favoriteRepository.deleteByMemberIdAndStoreId(memberId, storeId);

            return false; //삭제
        } else {

            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

            Store store = storeRepository.findById(storeId)
                    .orElseThrow(() -> new IllegalArgumentException("업소를 찾을 수 없습니다."));

            // Member 객체를 사용하여 Favorite 생성
            Favorite favorite = Favorite.create(member, store);
            favoriteRepository.save(favorite);
            return true; // 추가
        }
    }

    // 내 즐겨찾기 목록 조회
    @Transactional(readOnly = true)
    public List<FavoriteResponseDto> getMyFavorites(Long memberId) {
        List<Favorite> favorites = favoriteRepository.findByMemberId(memberId);


        return favorites.stream()
                .map(favorite -> FavoriteResponseDto.builder()
                        .favoriteId(favorite.getId())
                        .storeId(favorite.getStore().getId())
                        .storeName(favorite.getStore().getStoreName())
                        //.businessType(favorite.getStore().getBusinessType())
                        .address(favorite.getStore().getAddress().getFullAddress())
                        .build())
                .toList();
    }

    // 즐겨찾기 여부 확인
    @Transactional(readOnly = true)
    public boolean isFavorite(Long memberId, Long storeId) {
        return favoriteRepository.existsByMemberIdAndStoreId(memberId, storeId);
    }

    // 업소별 즐겨찾기 수 조회
    @Transactional(readOnly = true)
    public long getFavoriteCount(Long storeId) {
        return favoriteRepository.countByStoreId(storeId);
    }
}
