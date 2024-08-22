package com.blackcoffee.shopapp.controller;

import com.blackcoffee.shopapp.dto.OrderDto;
import com.blackcoffee.shopapp.dto.OrderStatusDto;
import com.blackcoffee.shopapp.model.OrderStatus;
import com.blackcoffee.shopapp.response.OrderListResponse;
import com.blackcoffee.shopapp.response.OrderResponse;
import com.blackcoffee.shopapp.response.ProductResponse;
import com.blackcoffee.shopapp.services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> getAllOrders(@PathVariable("userId") long id, @RequestHeader("Authorization") String token){

        try{
            List<OrderResponse> orders=orderService.getAllOrders(id);
            return ResponseEntity.ok(orders);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/get-orders-by-keyword")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getOrdersByKeyword(@RequestParam(defaultValue = "") String keyword,
                                                @RequestParam(required = false,defaultValue = "0") int page, @RequestParam(required = false, defaultValue = "10") int limit,
                                                @RequestHeader("Authorization") String token){

        try{
            PageRequest pageable= PageRequest.of(page, limit, Sort.by("id").descending());
            Page<OrderResponse> orderResponses=orderService.findByKeyword(keyword, pageable);
            int totalPages=orderResponses.getTotalPages();
            long totalElements=orderResponses.getTotalElements();
            List<OrderResponse> orderResponseList=orderResponses.getContent();
            return ResponseEntity.ok(OrderListResponse.builder()
                            .orders(orderResponseList)
                            .totalElements(totalElements)
                            .totalPages(totalPages)
                    .build());
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderDto orderDto, BindingResult bindingResult){

        try{
            if(bindingResult.hasErrors()){
                List<String> errors =bindingResult.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
                return ResponseEntity.badRequest().body(errors);
            }
            OrderResponse savedOrder =orderService.createOrder(orderDto);
            return ResponseEntity.ok(savedOrder);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/order/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> getOrderById(@PathVariable("id") long id){

        try{
            OrderResponse order=orderService.getOrderById(id);
            return ResponseEntity.ok(order);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateOrder(@PathVariable("id") long id,@Valid @RequestBody OrderDto orderDto, BindingResult bindingResult){
        try{
            if(bindingResult.hasErrors()){
                List<String> errors =bindingResult.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
                return ResponseEntity.badRequest().body(errors);
            }
            OrderResponse order=orderService.updateOrder(id,orderDto);
            return ResponseEntity.ok(order);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateOrderStatus(@PathVariable("id") long id, @Valid @RequestBody OrderStatusDto orderStatusDto, BindingResult bindingResult,@RequestHeader("Authorization") String token){
        try{
            if(bindingResult.hasErrors()){
                List<String> errors =bindingResult.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
                return ResponseEntity.badRequest().body(errors);
            }
            OrderResponse order=orderService.updateStatus(id,orderStatusDto.getStatus());
            return ResponseEntity.ok(order);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteOrderById(@PathVariable("id") long id){

        try{
            orderService.deleteOrder(id);
            return ResponseEntity.ok("Deleted order successfully.");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
