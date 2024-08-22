package com.blackcoffee.shopapp.dto;

import com.blackcoffee.shopapp.model.Product;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductImageDto {
    @JsonProperty("product_id")
    @Min(value=1, message = "Product Id must be greater than 1")
    private Long productId;
    @JsonProperty("image_url")
    @Lob
    private byte[] imageUrl;

}
