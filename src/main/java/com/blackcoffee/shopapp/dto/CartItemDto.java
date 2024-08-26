package com.blackcoffee.shopapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDto {
    @JsonProperty("product_id")
    private Long productId;
    private int quantity;
}
