package com.blackcoffee.shopapp.services;

import com.blackcoffee.shopapp.model.Coupon;
import com.blackcoffee.shopapp.request.CouponRequest;
import com.blackcoffee.shopapp.response.CouponResponse;

import java.util.List;

public interface CouponService {
    Coupon createCoupon(CouponRequest couponRequest);
    void deleteCoupon(Long id);
    Double calculateAmount(String code, Float totalAmount);
    List<Coupon> getAllCoupons();
    CouponResponse getCouponDetailsById(Long id);
}
