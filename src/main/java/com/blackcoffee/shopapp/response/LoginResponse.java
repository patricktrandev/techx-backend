package com.blackcoffee.shopapp.response;

import com.blackcoffee.shopapp.model.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {

    private String message;
    private String token;
    @JsonProperty("token_type")
    private String tokenType = "Bearer";
    private Long id;
    private String username;
    private Role roles;
}
