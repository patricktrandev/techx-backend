package com.blackcoffee.shopapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDto {
    @JsonProperty("phone_number")
    private String phoneNumber;
    @JsonProperty("email")
    private String email;
    @NotBlank(message = "Password is required")
    private String password;
    @JsonProperty("role_id")
    private Long roleId;
    @JsonProperty("facebook_account_id")
    private String facebookAccountId;
    @JsonProperty("google_account_id")
    private String googleAccountId;
    @JsonProperty("fullname")
    private String fullname;
    @JsonProperty("profile_image")
    private String profileImage;

    public boolean isPasswordBlank() {
        return password == null || password.trim().isEmpty();
    }

    public boolean isFacebookAccountIdValid() {
        return facebookAccountId != null && !facebookAccountId.isEmpty();
    }


    public boolean isGoogleAccountIdValid() {
        return googleAccountId != null && !googleAccountId.isEmpty();
    }
}
