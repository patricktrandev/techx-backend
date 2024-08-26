package com.blackcoffee.shopapp.services;

import com.blackcoffee.shopapp.dto.UserDto;
import com.blackcoffee.shopapp.dto.UserLoginDto;
import com.blackcoffee.shopapp.exception.InvalidParamsException;
import com.blackcoffee.shopapp.exception.PermissionDenyException;
import com.blackcoffee.shopapp.model.User;
import com.blackcoffee.shopapp.response.UserResponse;
import jakarta.mail.MessagingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    User createUser(UserDto userDto) throws Exception ;
    Boolean existByEmail(String email);
    void checkTokenValid(String otp, String email);
    void requestResetPassword(String email)throws MessagingException;
    void resetPassword(String newPassword, String email);
    void changePassword(Long userId, String newPassword);
    void deleteUserByAdmin(Long userId);
    String loginUser(UserLoginDto userLoginDto) throws InvalidParamsException;
    User getUserDetailsFromToken(String token)  throws Exception ;
    UserResponse updateUserInfo(String token, UserDto userDto)throws PermissionDenyException;
    Page<UserResponse> getAlluserAdmin(String keyword, Pageable pageable);
    UserResponse updateRole(Long roleId,Long id) throws Exception;
    UserResponse getUserDetailsById(Long id);

}
