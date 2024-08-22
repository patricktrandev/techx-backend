package com.blackcoffee.shopapp.services.impl;

import com.blackcoffee.shopapp.dto.CartItemDto;
import com.blackcoffee.shopapp.dto.OrderDto;
import com.blackcoffee.shopapp.exception.DataNotFoundException;
import com.blackcoffee.shopapp.model.*;
import com.blackcoffee.shopapp.repository.DetailsOrderRepository;
import com.blackcoffee.shopapp.repository.OrderRepository;
import com.blackcoffee.shopapp.repository.ProductRepository;
import com.blackcoffee.shopapp.repository.UserRepository;
import com.blackcoffee.shopapp.response.DetailsOrderResponse;
import com.blackcoffee.shopapp.response.OrderResponse;
import com.blackcoffee.shopapp.response.ProductResponse;
import com.blackcoffee.shopapp.services.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final DetailsOrderRepository detailsOrderRepository;
    private final ProductRepository productRepository;
    @Override
    @Transactional
    public OrderResponse createOrder(OrderDto orderDto) {
        User foundUser =userRepository.findById(orderDto.getUserId()).orElseThrow(()-> new DataNotFoundException("User does not exist with id="+orderDto.getUserId()));
        Order createdOrder=mapToModel(orderDto);
        createdOrder.setUser(foundUser);
        Order savedOrder =orderRepository.save(createdOrder);
        OrderResponse orderResponse=mapToResponse(savedOrder);
        List<DetailsOrder> detailsOrders= new ArrayList<>();
        for(CartItemDto order:orderDto.getCartItemDto()){
            Product product =productRepository.findById(order.getProductId()).orElseThrow(()->new DataNotFoundException("Cannot find product."));
            detailsOrders.add(DetailsOrder.builder()
                            .order(savedOrder)
                            .totalPayment(order.getQuantity()*product.getPrice())
                            .price(product.getPrice())
                            .numberOfProduct(order.getQuantity())
                            .color("")
                            .product(product)
                            .build());
        }
        List<DetailsOrder>detailsOrderList=detailsOrderRepository.saveAll(detailsOrders);

        return orderResponse;
    }

    private OrderResponse mapToResponse(Order savedOrder) {
        OrderResponse orderResponse=OrderResponse.builder()
                .id(savedOrder.getId())
                .userId(savedOrder.getUser().getId())
                .orderDetails(savedOrder.getOrderDetails())
                .fullName(savedOrder.getFullName())
                .email(savedOrder.getEmail())
                .isActive(savedOrder.getIsActive())
                .phoneNumber(savedOrder.getPhoneNumber())
                .note(savedOrder.getNote())
                .status(savedOrder.getOrderStatus())
                .orderDate(savedOrder.getOrderDate())
                .trackingNumber(savedOrder.getTrackingNumber())
                .totalPayment(savedOrder.getTotalPayment())
                .shippingMethod(savedOrder.getShippingMethod())
                .shippingAddress(savedOrder.getShippingAddress())
                .status(savedOrder.getOrderStatus())
                .shippingDate(savedOrder.getShippingDate())
                .paymentMethod(savedOrder.getPaymentMethod())
                .build();
        orderResponse.setCreatedAt(savedOrder.getCreatedAt());
        orderResponse.setUpdatedAt(savedOrder.getUpdatedAt());
        return orderResponse;
    }

    private Order mapToModel(OrderDto orderDto) {
        Order createdOrder=Order.builder()

                .fullName(orderDto.getFullName())

                .isActive(1)
                .phoneNumber(orderDto.getPhoneNumber())
                .email(orderDto.getEmail())
                .orderStatus(OrderStatus.PENDING)
                .trackingNumber(UUID.randomUUID().toString())
                .note(orderDto.getNote())
                .totalPayment(orderDto.getTotalPayment())
                .shippingMethod(orderDto.getShippingMethod())
                .paymentMethod(orderDto.getPaymentMethod())
                .shippingAddress(orderDto.getShippingAddress())
                .orderDate(LocalDateTime.now())
                .shippingDate(LocalDateTime.now().plusDays(2))
                .build();


        return createdOrder;
    }

    @Override
    public OrderResponse getOrderById(Long id) {
        Order order=orderRepository.findById(id).orElseThrow(()-> new DataNotFoundException("Order does not exist with id "+id));
        return mapToResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse updateOrder(Long id, OrderDto orderDto) {
        User foundUser =userRepository.findById(orderDto.getUserId()).orElseThrow(()-> new DataNotFoundException("User does not exist with id="+orderDto.getUserId()));
        Order order=orderRepository.findById(id).orElseThrow(()-> new DataNotFoundException("Order does not exist with id "+id));
        if(order!=null){
            order.setFullName(orderDto.getFullName());
            order.setEmail(orderDto.getEmail());
            order.setPhoneNumber(orderDto.getPhoneNumber());
            order.setShippingAddress(orderDto.getShippingAddress());
            order.setNote(orderDto.getNote());
            order.setShippingMethod(orderDto.getShippingMethod());
            order.setPaymentMethod(orderDto.getPaymentMethod());
        }

        Order savedOrder =orderRepository.save(order);
        OrderResponse orderResponse=mapToResponse(savedOrder);

        return orderResponse;
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        //temporary delete
       Order order=orderRepository.findById(id).orElseThrow(()-> new DataNotFoundException("Order does not exist with id "+id));
//        order.setIsActive(0);
        orderRepository.delete(order);
    }

    @Override
    public List<OrderResponse> getAllOrders(Long userId) {
        List<Order> orderList=orderRepository.findByUserId( userId);
        List<OrderResponse> orderResponsesList= new ArrayList<>();
        for(Order order: orderList){
            orderResponsesList.add(mapToResponse(order));
        }
        return orderResponsesList;
    }

    @Override
    public OrderResponse updateStatus(Long id, String status) {

        Order order=orderRepository.findById(id).orElseThrow(()-> new DataNotFoundException("Order does not exist with id "+id));
        order.setOrderStatus(status);
        Order updatedOrder=orderRepository.save(order);
        return mapToResponse(updatedOrder);
    }

    @Override
    public Page<OrderResponse> findByKeyword(String keyword, PageRequest pageRequest) {
        return orderRepository.findByKeyword(keyword,pageRequest).map(order -> {
            OrderResponse response=OrderResponse.builder()
                    .id(order.getId())
                    .orderDate(order.getOrderDate())
                    .status(order.getOrderStatus())
                    .shippingAddress(order.getShippingAddress())
                    .shippingDate(order.getShippingDate())
                    .paymentMethod(order.getPaymentMethod())
                    .note(order.getNote())
                    .email(order.getEmail())
                    .trackingNumber(order.getTrackingNumber())
                    .isActive(order.getIsActive())
                    .fullName(order.getFullName())
                    .orderDetails(order.getOrderDetails())
                    .totalPayment(order.getTotalPayment())
                    .shippingMethod(order.getShippingMethod())
                    .userId(order.getUser().getId())
                    .build();
            response.setCreatedAt(order.getCreatedAt());
            response.setUpdatedAt(order.getUpdatedAt());
            return response;
        });

    }
}
