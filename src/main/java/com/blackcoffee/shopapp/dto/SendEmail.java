package com.blackcoffee.shopapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendEmail {
    @NotBlank(message = "Email is required")
    private String email;
}
