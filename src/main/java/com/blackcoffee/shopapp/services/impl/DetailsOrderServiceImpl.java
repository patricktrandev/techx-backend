package com.blackcoffee.shopapp.services.impl;

import com.blackcoffee.shopapp.dto.OrderDetailsDto;
import com.blackcoffee.shopapp.exception.DataNotFoundException;
import com.blackcoffee.shopapp.model.DetailsOrder;
import com.blackcoffee.shopapp.model.Order;
import com.blackcoffee.shopapp.model.Product;
import com.blackcoffee.shopapp.repository.DetailsOrderRepository;
import com.blackcoffee.shopapp.repository.OrderRepository;
import com.blackcoffee.shopapp.repository.ProductRepository;

import com.blackcoffee.shopapp.response.DetailsOrderResponse;
import com.blackcoffee.shopapp.response.OrderResponse;
import com.blackcoffee.shopapp.services.DetailsOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DetailsOrderServiceImpl implements DetailsOrderService {
    private final DetailsOrderRepository detailsOrderRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    @Override
    public DetailsOrderResponse createOrderDetails(OrderDetailsDto orderDetailsDto) {
        Order order=orderRepository.findById(orderDetailsDto.getOrderId()).orElseThrow(()-> new DataNotFoundException("Order does not exist with id="+orderDetailsDto.getOrderId()));
        Product foundProduct= productRepository.findById(orderDetailsDto.getProductId()).orElseThrow(()-> new DataNotFoundException("Order does not exist with id="+orderDetailsDto.getProductId()));
        DetailsOrder detailsOrder=mapToModel(orderDetailsDto);
        detailsOrder.setOrder(order);
        detailsOrder.setProduct(foundProduct);
        DetailsOrder savedOrderDetails=detailsOrderRepository.save(detailsOrder);
        return mapToResponse(savedOrderDetails);


    }

    private DetailsOrderResponse mapToResponse(DetailsOrder savedOrderDetails) {
        return DetailsOrderResponse.builder()
                .id(savedOrderDetails.getId())
                .order(savedOrderDetails.getOrder().getId())
                .product(savedOrderDetails.getProduct().getId())
                .price(savedOrderDetails.getPrice())
                .numberOfProduct(savedOrderDetails.getNumberOfProduct())
                .totalPayment(savedOrderDetails.getTotalPayment())
                .color(savedOrderDetails.getColor())
                .build();
    }

    private DetailsOrder mapToModel(OrderDetailsDto orderDetailsDto) {
        return DetailsOrder.builder()
                .price(orderDetailsDto.getPrice())
                .numberOfProduct(orderDetailsDto.getNumberOfProduct())
                .totalPayment(orderDetailsDto.getTotalPayment())
                .color(orderDetailsDto.getColor())
                .build();
    }

    @Override
    public List<DetailsOrderResponse> getOrderDetailsByOrder(Long id) {
        List<DetailsOrder> detailsOrderList= detailsOrderRepository.findByOrderId(id);
        List<DetailsOrderResponse> orderResponseList= new ArrayList<>();
        for(DetailsOrder detailsOrder: detailsOrderList){
            orderResponseList.add(mapToResponse(detailsOrder));
        }
        return orderResponseList;
    }

    @Override
    public DetailsOrderResponse updateOrderDetails(Long id, OrderDetailsDto orderDetailsDto) {
        Order order=orderRepository.findById(orderDetailsDto.getOrderId()).orElseThrow(()-> new DataNotFoundException("Order does not exist with id="+orderDetailsDto.getOrderId()));
        Product foundProduct= productRepository.findById(orderDetailsDto.getProductId()).orElseThrow(()-> new DataNotFoundException("Order does not exist with id="+orderDetailsDto.getProductId()));
        DetailsOrder detailsOrder=detailsOrderRepository.findById(id).orElseThrow(()-> new DataNotFoundException("Item order does not exist with id="+id));
        detailsOrder.setPrice(orderDetailsDto.getPrice());
        detailsOrder.setNumberOfProduct(orderDetailsDto.getNumberOfProduct());
        detailsOrder.setTotalPayment(orderDetailsDto.getTotalPayment());
        detailsOrder.setColor(orderDetailsDto.getColor());
        DetailsOrder updatedDetails=detailsOrderRepository.save(detailsOrder);
        return mapToResponse(updatedDetails);
    }

    @Override
    public void deleteOrderDetails(Long id) {
        //
        DetailsOrder detailsOrder=detailsOrderRepository.findById(id).orElseThrow(()-> new DataNotFoundException("Item order does not exist with id="+id));
        detailsOrderRepository.delete(detailsOrder);
    }

    @Override
    public DetailsOrderResponse getOrderDetails(Long id) {
        DetailsOrder detailsOrder=detailsOrderRepository.findById(id).orElseThrow(()-> new DataNotFoundException("Item order does not exist with id="+id));
        return mapToResponse(detailsOrder);
    }
}
