package com.blackcoffee.shopapp.response;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CouponDiscountResponse {
    private Double discount;
    private String errorMessage;

}
