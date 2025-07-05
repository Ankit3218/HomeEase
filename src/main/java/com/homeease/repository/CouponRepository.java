package com.homeease.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.homeease.entity.Coupon;

import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Integer> {
    List<Coupon> findByActiveTrue();
    Optional<Coupon> findByCode(String code);
    Optional<Coupon> findByCodeAndActiveTrue(String code);

}

