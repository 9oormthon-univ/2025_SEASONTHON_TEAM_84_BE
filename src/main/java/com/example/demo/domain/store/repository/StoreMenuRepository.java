package com.example.demo.domain.store.repository;

import com.example.demo.domain.store.entity.StoreMenu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreMenuRepository extends JpaRepository<StoreMenu, Long> {
}
