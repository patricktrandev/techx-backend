package com.blackcoffee.shopapp.response;

import com.blackcoffee.shopapp.model.CouponCondition;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CouponResponse {
    @JsonProperty("coupon_id")
    private Long id;
    @JsonProperty("coupon")
    @NotBlank(message = "Coupon name is required")
    private String name;
    private int active;
    private List<CouponCondition> couponCondition;
}
