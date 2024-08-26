package com.blackcoffee.shopapp.services.impl;

import com.blackcoffee.shopapp.dto.CouponDto;
import com.blackcoffee.shopapp.exception.DataNotFoundException;
import com.blackcoffee.shopapp.model.Coupon;
import com.blackcoffee.shopapp.model.CouponCondition;
import com.blackcoffee.shopapp.repository.CouponConditionRepository;
import com.blackcoffee.shopapp.repository.CouponRepository;
import com.blackcoffee.shopapp.request.CouponRequest;
import com.blackcoffee.shopapp.response.CouponResponse;
import com.blackcoffee.shopapp.services.CouponService;
import com.blackcoffee.shopapp.utils.AttributeCoupon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {
    private final CouponRepository couponRepository;
    private final CouponConditionRepository couponConditionRepository;
    @Override
    public Coupon createCoupon(CouponRequest couponRequest) {
        Coupon coupon=Coupon.builder()
                .code(couponRequest.getName())
                .active(1)
                .build();
        Coupon newCoupon= couponRepository.save(coupon);

        CouponCondition couponCondition=CouponCondition.builder()
                .coupon(newCoupon)
                .value(couponRequest.getValue())
                .attribute(couponRequest.getAttribute())
                .operator(couponRequest.getOperator())
                .discountAmount(couponRequest.getDiscountAmount())
                .build();



        CouponCondition newCouponCondition=couponConditionRepository.save(couponCondition);

        //return mapToResponse(newCouponCondition, newCoupon);
        return newCoupon;
    }
    Coupon mapToModel(CouponDto couponDto){
        return Coupon.builder()
                .code(couponDto.getName())
                .active(1)
                .build();
    }
    //    CouponCondition mapCouponConditionModel(List<CouponConditionDto> couponConditionDto){
//
//        return CouponCondition.builder()
//
//                .attribute(couponConditionDto.getAttribute())
//                .operator(couponConditionDto.getOperator())
//                .value(couponConditionDto.getValue())
//                .discountAmount(couponConditionDto.getDiscountAmount())
//                .build();
//
//    }
    CouponResponse mapToResponse(Coupon coupon, List<CouponCondition> couponCondition){
        return CouponResponse.builder()
                .name(coupon.getCode())
                .id(coupon.getId())
                .active(coupon.getActive())
                .couponCondition(couponCondition)
                .build();
    }

    @Override
    public void deleteCoupon(Long id) {
        Coupon coupon=couponRepository.findById(id).orElseThrow(()-> new DataNotFoundException("Coupon is noot found with id: "+id));
        coupon.setActive(0);
        couponRepository.save(coupon);


    }

    @Override
    public Double calculateAmount(String code, Float totalAmount) {
        Coupon coupon=couponRepository.findByCode(code).orElseThrow(() -> new IllegalArgumentException("Coupon not found"));
        if (coupon.getActive()!=1) {
            throw new IllegalArgumentException("Coupon is not active");
        }
        double discount = calculateDiscount(coupon, totalAmount);
        double finalAmount = totalAmount - discount;
        return finalAmount;

    }
    private double calculateDiscount(Coupon coupon, double totalAmount){
        List<CouponCondition> couponConditions= couponConditionRepository.findByCouponId(coupon.getId());

        double discount=0.0;
        double updatedTotalAmount= totalAmount;
        for(CouponCondition c:couponConditions){
            String attribute=c.getAttribute();
            String operator=c.getOperator();
            String value= c.getValue();
            Double percentDiscount= Double.valueOf(String.valueOf(c.getDiscountAmount()));
            if(attribute.equalsIgnoreCase((AttributeCoupon.MINIMUM_AMOUNT).name())){
                if(operator.equalsIgnoreCase(">") && updatedTotalAmount > Double.parseDouble(value)){
                    discount+=updatedTotalAmount * percentDiscount /100;
                }
            }else if (attribute.equalsIgnoreCase(AttributeCoupon.APPLICABLE_DATE.name())){
                LocalDate applicableDate= LocalDate.parse(value);
                LocalDate currentDate= LocalDate.now();
                if(operator.equalsIgnoreCase("BEFORE") && currentDate.isBefore(applicableDate)){
                    discount+=updatedTotalAmount *percentDiscount/100;
                }
            }
            updatedTotalAmount= updatedTotalAmount-discount;



        }
        return discount;
    }

    @Override
    public List<Coupon> getAllCoupons() {
        List<Coupon> coupons=couponRepository.findAll();
//        List<CouponResponse> couponResponses= new ArrayList<>();
//        for(Coupon c: coupons){
//            couponResponses.add(c);
//        }
        return coupons;
    }

    @Override
    public CouponResponse getCouponDetailsById(Long id) {
        Coupon coupon=couponRepository.findById(id).orElseThrow(()-> new DataNotFoundException("Coupon is noot found with id: "+id));
        List<CouponCondition> couponCondition=couponConditionRepository.findByCouponId(coupon.getId());
        return mapToResponse(coupon, couponCondition);
    }
}