package com.blackcoffee.shopapp.repository;

import com.blackcoffee.shopapp.model.CouponCondition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouponConditionRepository extends JpaRepository<CouponCondition, Long> {
    List<CouponCondition> findByCouponId(Long id);
}
