package com.tabling_batch.domain.repository;

import com.tabling_batch.domain.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopRepository extends JpaRepository<Shop, Long> {

}
