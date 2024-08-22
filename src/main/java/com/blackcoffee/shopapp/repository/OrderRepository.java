package com.blackcoffee.shopapp.repository;

import com.blackcoffee.shopapp.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
   List<Order> findByUserId(Long userId);
   boolean existsByTrackingNumber(String trackingNumber);
   @Query("SELECT o from Order o WHERE " +
           "(:keyword is null or :keyword = '' or o.email LIKE %:keyword% or o.trackingNumber LIKE %:keyword% or  o.fullName LIKE %:keyword% or o.shippingAddress like %:keyword% " +
           "or  o.note like %:keyword%)")
   Page<Order> findByKeyword(String keyword, Pageable pageable);
}
