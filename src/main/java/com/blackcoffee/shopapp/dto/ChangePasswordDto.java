package com.blackcoffee.shopapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordDto {
    @JsonProperty(value = "user_id")
    private Long userId;
    @JsonProperty(value = "new_password")
    private String newPassword;
}
