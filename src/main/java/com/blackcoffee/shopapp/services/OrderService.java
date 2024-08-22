package com.blackcoffee.shopapp.services;

import com.blackcoffee.shopapp.dto.OrderDto;
import com.blackcoffee.shopapp.response.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderDto orderDto);
    OrderResponse getOrderById(Long id);
    OrderResponse updateOrder(Long id, OrderDto orderDto);
    void deleteOrder(Long id);
    List<OrderResponse> getAllOrders(Long userId);
    OrderResponse updateStatus(Long id, String status);
    Page<OrderResponse> findByKeyword(String keyword, PageRequest pageRequest);
}
