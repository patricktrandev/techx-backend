package com.blackcoffee.shopapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailsDto {

    @JsonProperty("order_id")
    @Min(value = 1, message = "Order id must be greater than 0")
    private Long orderId;
    @JsonProperty("product_id")
    @Min(value = 1, message = "Product id must be greater than 0")
    private Long productId;
    private Float price;
    @JsonProperty("number_of_product")
    @Min(value = 1, message = "Number of products must be greater than 1")
    private int numberOfProduct;
    @JsonProperty("total_payment")
    @Min(value = 0, message = "Total payment must be greater or equal to 0")
    private Float totalPayment;
    private String color;
}
