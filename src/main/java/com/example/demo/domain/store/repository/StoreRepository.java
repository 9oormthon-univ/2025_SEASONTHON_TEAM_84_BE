package com.example.demo.domain.store.repository;

import com.example.demo.domain.store.entity.Category;
import com.example.demo.domain.store.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Store 엔티티에 대한 리포지토리 인터페이스
 */
public interface StoreRepository extends JpaRepository<Store, Long> {

    @Query("select s from Store s left join fetch s.menus where s.id = :id")
    Optional<Store> findByIdFetchMenu(@Param("id") Long id);

    /**
     * 업소명으로 검색
     */
    Optional<Store> findByStoreName(String storeName);

    /**
     * 업소명 포함 검색 (활성화된 업소만)
     */
    Page<Store> findByStoreNameContainingAndIsActiveTrue(String storeName, Pageable pageable);

    /**
     * 업종별 검색 (활성화된 업소만)
     */
    Page<Store> findByCategoryAndIsActiveTrue(Category category, Pageable pageable);

    /**
     * 지역별 검색 (시도, 시군)
     */
    @Query("SELECT s FROM Store s WHERE s.address.sido LIKE %:sido% AND s.address.sigun LIKE %:sigun% AND s.isActive = true")
    Page<Store> findByRegion(@Param("sido") String sido, @Param("sigun") String sigun, Pageable pageable);

    /**
     * 시도별 검색 (활성화된 업소만)
     */
    @Query("SELECT s FROM Store s WHERE s.address.sido LIKE %:sido% AND s.isActive = true")
    Page<Store> findBySido(@Param("sido") String sido, Pageable pageable);

    /**
     * 좌표 기반 반경 검색 (Haversine 공식 사용)
     */
    @Query(value = """
        SELECT s.*, 
               (6371 * acos(cos(radians(:latitude)) * cos(radians(s.latitude)) * 
               cos(radians(s.longitude) - radians(:longitude)) + 
               sin(radians(:latitude)) * sin(radians(s.latitude)))) AS distance
        FROM store s 
        WHERE s.latitude IS NOT NULL 
          AND s.longitude IS NOT NULL 
          AND s.is_active = true
        HAVING distance <= :radiusKm 
        ORDER BY distance
        """, 
        countQuery = """
        SELECT COUNT(s.store_id)
        FROM store s 
        WHERE s.latitude IS NOT NULL 
          AND s.longitude IS NOT NULL 
          AND s.is_active = true
          AND (6371 * acos(cos(radians(:latitude)) * cos(radians(s.latitude)) * 
               cos(radians(s.longitude) - radians(:longitude)) + 
               sin(radians(:latitude)) * sin(radians(s.latitude)))) <= :radiusKm
        """,
        nativeQuery = true)
    Page<Store> findStoresWithinRadius(
        @Param("latitude") Double latitude, 
        @Param("longitude") Double longitude, 
        @Param("radiusKm") Double radiusKm, 
        Pageable pageable
    );

    /**
     * 복합 검색: 업종 + 지역
     */
    @Query("SELECT s FROM Store s WHERE s.category = :category " +
           "AND s.address.sido LIKE %:sido% AND s.address.sigun LIKE %:sigun% " +
           "AND s.isActive = true")
    Page<Store> findByCategoryTypeAndRegion(
        @Param("category") Category category,
        @Param("sido") String sido,
        @Param("sigun") String sigun,
        Pageable pageable
    );

    /**
     * 복합 검색: 업소명 + 업종 + 지역
     */
    @Query("SELECT s FROM Store s WHERE s.storeName LIKE %:storeName% " +
           "AND s.category = :category " +
           "AND s.address.sido LIKE %:sido% " +
           "AND s.isActive = true")
    Page<Store> findByStoreNameAndCategoryAndSido(
        @Param("storeName") String storeName,
        @Param("category") Category category,
        @Param("sido") String sido,
        Pageable pageable
    );

    /**
     * 활성화된 모든 업소 조회
     */
    Page<Store> findByIsActiveTrue(Pageable pageable);

    /**
     * 연락처로 검색
     */
    Optional<Store> findByContactNumber(String contactNumber);

    /**
     * 좌표 정보가 있는 업소만 조회 (지도 표시용)
     */
    @Query("SELECT s FROM Store s WHERE s.address.latitude IS NOT NULL " +
           "AND s.address.longitude IS NOT NULL AND s.isActive = true")
    List<Store> findStoresWithCoordinates();

    /**
     * 좌표 정보가 없는 업소 조회 (좌표 보완 필요)
     */
    @Query("SELECT s FROM Store s WHERE (s.address.latitude IS NULL " +
           "OR s.address.longitude IS NULL) AND s.isActive = true")
    List<Store> findStoresWithoutCoordinates();

    /**
     * 업종별 업소 수 조회
     */
    @Query("SELECT s.category, COUNT(s) FROM Store s WHERE s.isActive = true GROUP BY s.category")
    List<Object[]> countByCategory();

    /**
     * 지역별 업소 수 조회
     */
    @Query("SELECT s.address.sido, s.address.sigun, COUNT(s) FROM Store s " +
           "WHERE s.isActive = true GROUP BY s.address.sido, s.address.sigun")
    List<Object[]> countByRegion();
}
