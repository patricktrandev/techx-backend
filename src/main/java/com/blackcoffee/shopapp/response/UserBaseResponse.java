package com.blackcoffee.shopapp.response;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserBaseResponse {
    private UserResponse user;
    private String message;
}
