package com.example.demo.domain.review.repository;

import com.example.demo.domain.review.entity.Review;
import com.example.demo.domain.store.entity.Store;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    //List<Review> findByStore(Store store);

    List<Review> findByStoreAndIsDeletedFalse(Store store);

    Optional<Review> findByIdAndIsDeletedFalse(Long id);

    List<Review> findByWriterIdAndIsDeletedFalse(Long writerId);

    List<Review> findByStoreIdAndIsDeletedFalse(Long storeId);

}
