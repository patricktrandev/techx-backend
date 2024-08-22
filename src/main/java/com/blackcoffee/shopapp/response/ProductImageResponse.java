package com.blackcoffee.shopapp.response;

import com.blackcoffee.shopapp.model.Product;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductImageResponse {
    private Long id;
    @JsonProperty("product_id")
    private Long productId;
    @JsonProperty("image_url")
    private String imageUrl;

}
