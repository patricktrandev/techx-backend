package com.blackcoffee.shopapp.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DetailsOrderResponse {
    private Long id;
    private Float price;
    @JsonProperty("number_of_product")
    private int numberOfProduct;
    @JsonProperty("total_payment")
    private Float totalPayment;
    private String color;
    @JsonProperty("order_id")
    private Long order;
    @JsonProperty("product_id")
    private Long product;
}
