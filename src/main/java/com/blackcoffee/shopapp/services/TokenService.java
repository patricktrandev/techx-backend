package com.blackcoffee.shopapp.services;

import com.blackcoffee.shopapp.model.Token;
import com.blackcoffee.shopapp.model.User;
import com.blackcoffee.shopapp.response.UserResponse;

public interface TokenService {
    Token addToken(User user, String token, String deviceType);
}
