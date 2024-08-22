package com.blackcoffee.shopapp.services;

import com.blackcoffee.shopapp.dto.UserDto;
import com.blackcoffee.shopapp.exception.InvalidParamsException;
import com.blackcoffee.shopapp.exception.PermissionDenyException;
import com.blackcoffee.shopapp.model.User;
import com.blackcoffee.shopapp.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    User createUser(UserDto userDto) throws Exception ;
    String loginUser(String phoneNumber, String password, Long roleId) throws InvalidParamsException;
    UserResponse getUserDetailsFromToken(String token)  throws Exception ;
    UserResponse updateUserInfo(String token, UserDto userDto)throws PermissionDenyException;
    Page<UserResponse> getAlluserAdmin(String keyword, Pageable pageable);
}
