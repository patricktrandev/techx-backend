package com.blackcoffee.shopapp.controller;

import com.blackcoffee.shopapp.model.Coupon;
import com.blackcoffee.shopapp.request.CouponRequest;
import com.blackcoffee.shopapp.response.CouponDiscountResponse;
import com.blackcoffee.shopapp.response.CouponResponse;
import com.blackcoffee.shopapp.services.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/coupons")
@RequiredArgsConstructor
@Tag(name = "CRUD REST API for Coupon Resource")
public class CouponController {

    private final CouponService couponService;
    @Operation(
            summary = "Get coupon details by admin"
    )
    @GetMapping("/{id}")
    public ResponseEntity<CouponResponse> getCouponByAdmin(@PathVariable("id")Long id ){
        return ResponseEntity.ok(couponService.getCouponDetailsById(id));
    }
    @Operation(
            summary = "Get all Coupon by admin"
    )
    @GetMapping()
    public ResponseEntity<List<Coupon>> getAllCouponsByAdmin(){
        return ResponseEntity.ok(couponService.getAllCoupons());
    }
    @Operation(
            summary = "Calculate discount by all users"
    )
    @GetMapping("/calculate")
    public ResponseEntity<CouponDiscountResponse> getDiscountByCoupon(@RequestParam String coupon, @RequestParam Float amount){
        try{
            Double finalAmount= couponService.calculateAmount(coupon,amount);
            return ResponseEntity.ok(
                    CouponDiscountResponse.builder()
                            .discount(finalAmount).build());
        }catch (Exception e){
            return ResponseEntity.badRequest().body(CouponDiscountResponse.builder()
                    .errorMessage(e.getMessage())
                    .build());
        }
    }
    @Operation(
            summary = "Create new Coupon by admin"
    )
    @PostMapping()
    public ResponseEntity<?> createCoupon(@Valid @RequestBody CouponRequest couponRequest, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            List<String> errors =bindingResult.getFieldErrors().stream().map(e->e.getDefaultMessage()).toList();
            return ResponseEntity.badRequest().body(errors);
        }
        Coupon coupon= couponService.createCoupon(couponRequest);
        return new ResponseEntity<>(coupon, HttpStatus.CREATED);
    }
    @Operation(
            summary = "Delete Coupon by admin"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCouponByAdmin(@PathVariable("id")Long id ){
        couponService.deleteCoupon(id);
        return ResponseEntity.ok("Delete coupon successfully");
    }
}
