package com.blackcoffee.shopapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordDto {
    @JsonProperty(value = "new_password")
    private String newPassword;
    private String email;

}
