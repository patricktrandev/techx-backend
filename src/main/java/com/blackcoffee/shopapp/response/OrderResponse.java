package com.blackcoffee.shopapp.response;

import com.blackcoffee.shopapp.model.DetailsOrder;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse extends BaseResponse {
    private Long id;
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("fullname")
    private String fullName;
    private String email;
    @JsonProperty("phone_number")
    private String phoneNumber;
    @JsonProperty("trackingNumber")
    private String trackingNumber;

    private String note;
    private String status;
    @JsonProperty("order_date")
    private LocalDateTime orderDate;
    @JsonProperty("total_payment")
    private Float totalPayment;
    @JsonProperty("shipping_method")
    private String shippingMethod;
    @JsonProperty("shipping_date")
    private LocalDateTime shippingDate;
    @JsonProperty("shipping_address")
    private String shippingAddress;
    @JsonProperty("payment_method")
    private String paymentMethod;
    @JsonProperty( "isActive")
    private int isActive;
    private List<DetailsOrder> orderDetails;
}
