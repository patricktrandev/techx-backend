package com.blackcoffee.shopapp.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public class CouponRequest {
    @JsonProperty("coupon")
    @NotBlank(message = "Coupon name is required")
    private String name;
    @JsonProperty("attribute")
    private String attribute;
    private String operator;
    private String value;
    @JsonProperty("discount_amount")
    private BigDecimal discountAmount;
}
