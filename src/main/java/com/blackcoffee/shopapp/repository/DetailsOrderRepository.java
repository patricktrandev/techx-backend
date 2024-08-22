package com.blackcoffee.shopapp.repository;

import com.blackcoffee.shopapp.model.DetailsOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetailsOrderRepository extends JpaRepository<DetailsOrder, Long> {
    List<DetailsOrder> findByOrderId(Long orderId);
}
