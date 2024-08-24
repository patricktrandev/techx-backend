package com.blackcoffee.shopapp.services;

import com.blackcoffee.shopapp.model.Token;
import com.blackcoffee.shopapp.response.UserResponse;

public interface TokenService {
    Token addToken(UserResponse user, String token, String deviceType);
}
