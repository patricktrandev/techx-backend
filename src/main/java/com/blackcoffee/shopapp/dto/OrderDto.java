package com.blackcoffee.shopapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("fullname")
    private String fullName;
    private String email;
    @NotBlank(message = "Phone number is required")
    @JsonProperty("phone_number")
    @Size(min=5, message = "Phone number must be at least 5 characters")
    private String phoneNumber;

    @JsonProperty("trackingNumber")
    private String trackingNumber;
    private String note;
    @JsonProperty("total_payment")
    @Min(value=0, message = "Total payment must be greater than 0")
    private Float totalPayment;
    @JsonProperty("shipping_method")
    private String shippingMethod;
    @JsonProperty("shipping_address")
    private String shippingAddress;
    @JsonProperty("payment_method")
    private String paymentMethod;
    @JsonProperty("cart_items")
    private List<CartItemDto> cartItemDto;

}
