package com.blackcoffee.shopapp.services;

import com.blackcoffee.shopapp.dto.OrderDetailsDto;
import com.blackcoffee.shopapp.response.DetailsOrderResponse;

import java.util.List;

public interface DetailsOrderService {
    DetailsOrderResponse createOrderDetails(OrderDetailsDto orderDetailsDto);
    DetailsOrderResponse getOrderDetails(Long id);
    DetailsOrderResponse updateOrderDetails(Long id, OrderDetailsDto orderDetailsDto);
    void deleteOrderDetails(Long id);
    List<DetailsOrderResponse> getOrderDetailsByOrder(Long orderId);
}
