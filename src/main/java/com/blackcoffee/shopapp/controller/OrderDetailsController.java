package com.blackcoffee.shopapp.controller;

import com.blackcoffee.shopapp.dto.OrderDetailsDto;
import com.blackcoffee.shopapp.response.DetailsOrderResponse;
import com.blackcoffee.shopapp.services.DetailsOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/order_details")
@RequiredArgsConstructor
@Tag(name = "CRUD REST API for Details Product of o Order Resource - Details Order Entity")
public class OrderDetailsController {
    private final DetailsOrderService detailsOrderService;
    @PostMapping
    @Operation(
            summary = "Create details order in an order"
    )
    public ResponseEntity<?> createOrderDetails(@Valid @RequestBody OrderDetailsDto orderDetailsDto, BindingResult bindingResult){
        try{

            if(bindingResult.hasErrors()){
                List<String> errors= bindingResult.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
                return ResponseEntity.badRequest().body(errors);
            }
            DetailsOrderResponse orderResponse =detailsOrderService.createOrderDetails(orderDetailsDto);
            return ResponseEntity.ok(orderResponse);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @Operation(
            summary = "Get details order by admin or user"
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderDetailsById(@PathVariable("id") long id){

        try{
            DetailsOrderResponse orderResponse=detailsOrderService.getOrderDetails(id);
            return ResponseEntity.ok(orderResponse);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/order/{id}")
    @Operation(
            summary = "Get list details order by admin"
    )
    public ResponseEntity<?> getOrderDetailsByOrderId(@PathVariable("id") long id){

        try{
            List<DetailsOrderResponse> orderResponseList=detailsOrderService.getOrderDetailsByOrder(id);
            return ResponseEntity.ok(orderResponseList);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @Operation(
            summary = "Update details order by admin"
    )
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrderDetails(@PathVariable("id") long id, @Valid @RequestBody OrderDetailsDto orderDetailsDto, BindingResult bindingResult){
        try{
            if(bindingResult.hasErrors()){
                List<String> errors =bindingResult.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
                return ResponseEntity.badRequest().body(errors);
            }
            DetailsOrderResponse updatedOrderResponse =detailsOrderService.updateOrderDetails(id,orderDetailsDto);
            return ResponseEntity.ok(updatedOrderResponse);

        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @Operation(
            summary = "Delete details order by admin"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrderDetailsById(@PathVariable("id") long id){

        try{
            detailsOrderService.deleteOrderDetails(id);
            return ResponseEntity.ok("Delete Order Details Successfully");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
