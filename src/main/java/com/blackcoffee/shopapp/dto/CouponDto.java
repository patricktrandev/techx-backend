package com.blackcoffee.shopapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CouponDto {
    @JsonProperty("coupon_id")
    private Long id;
    @JsonProperty("coupon")
    @NotBlank(message = "Coupon name is required")
    private String name;
    private int active;
}
