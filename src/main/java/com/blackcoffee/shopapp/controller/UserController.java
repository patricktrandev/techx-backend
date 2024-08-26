package com.blackcoffee.shopapp.controller;

import com.blackcoffee.shopapp.dto.*;
import com.blackcoffee.shopapp.model.Token;
import com.blackcoffee.shopapp.model.User;
import com.blackcoffee.shopapp.response.*;
import com.blackcoffee.shopapp.services.BaseRedisService;
import com.blackcoffee.shopapp.services.TokenService;
import com.blackcoffee.shopapp.services.UserService;
import com.blackcoffee.shopapp.utils.LocalizationUtils;
import com.blackcoffee.shopapp.utils.MessageKey;
import com.blackcoffee.shopapp.utils.ValidationUtils;
import com.blackcoffee.shopapp.utils.WebUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.apache.coyote.Response;
import org.hibernate.sql.Update;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;


import java.util.List;


@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
@Tag(name = "CRUD REST API for User Resource")
public class UserController {

    private final UserService userService;
    private final TokenService tokenService;
    private final BaseRedisService baseRedisService;
    private final MessageSource messageSource;
    private final LocaleResolver localResolver;
    private final LocalizationUtils localizationUtils;
    private final WebUtils webUtils;

    @Operation(
            summary = "Request send email to reset password."
    )
    @PostMapping("/reset/send-email")
    public ResponseEntity<?> sendEmailResetPassword(@Valid @RequestBody SendEmail sendEmail, BindingResult bindingResult){
        try{
            if(bindingResult.hasErrors()){
                List<String> errors =bindingResult.getFieldErrors().stream().map(e->e.getDefaultMessage()).toList();
                return ResponseEntity.badRequest().body(errors);
            }
            userService.requestResetPassword(sendEmail.getEmail());
            return ResponseEntity.ok("Please check your email. Email sent successfully!");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @Operation(
            summary = "Check Valid Token In Redis"
    )
    @PostMapping("/token-valid")
    public ResponseEntity<?> checkTokenValid(@Valid @RequestBody TokenResetPassword tokenResetPassword, BindingResult bindingResult){
        try{
            if(bindingResult.hasErrors()){
                List<String> errors =bindingResult.getFieldErrors().stream().map(e->e.getDefaultMessage()).toList();
                return ResponseEntity.badRequest().body(errors);
            }
            userService.checkTokenValid(tokenResetPassword.getOtp(), tokenResetPassword.getEmail());
            return ResponseEntity.ok("Token is valid.");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @Operation(
            summary = "Reset password"
    )
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordDto resetPasswordDto, BindingResult bindingResult){
        try{
            if(bindingResult.hasErrors()){
                List<String> errors =bindingResult.getFieldErrors().stream().map(e->e.getDefaultMessage()).toList();
                return ResponseEntity.badRequest().body(errors);
            }
            userService.resetPassword(resetPasswordDto.getNewPassword(), resetPasswordDto.getEmail());
            return ResponseEntity.ok("Reset password successfully.");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @Operation(
            summary = "Change password by admin or user"
    )
    @PostMapping("/change-password")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDto changePasswordDto, BindingResult bindingResult){
        try{
            if(bindingResult.hasErrors()){
                List<String> errors =bindingResult.getFieldErrors().stream().map(e->e.getDefaultMessage()).toList();
                return ResponseEntity.badRequest().body(errors);
            }
            userService.changePassword(changePasswordDto.getUserId(), changePasswordDto.getNewPassword());
            return ResponseEntity.ok("Reset password successfully.");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @Operation(
            summary = "Register new account"
    )
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDto userDto, BindingResult bindingResult){

        try{
            if(bindingResult.hasErrors()){
                List<String> errors =bindingResult.getFieldErrors().stream().map(e->e.getDefaultMessage()).toList();
                return ResponseEntity.badRequest().body(errors);
            }
            if(    !(userDto.getPassword().equals(userDto.getConfirmPassword()))){
                return ResponseEntity.badRequest().body(ResponseObject.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .data(null)
                        .message("Password does not match")
                );
            }
            if(userDto.getEmail()==null || userDto.getEmail().trim().isBlank()){
                if(userDto.getPhoneNumber() == null || userDto.getPhoneNumber().isBlank()){
                    return ResponseEntity.badRequest().body(ResponseObject.builder()
                            .status(HttpStatus.BAD_REQUEST)
                            .data(null)
                            .message("At least email or phone number is required")
                    );
                }else{
                    if(!ValidationUtils.isValidPhoneNumber(userDto.getPhoneNumber())){
                        throw new Exception("Invalid phone number");
                    }
                }
            }else{
                if(!ValidationUtils.isValidEmail(userDto.getEmail())){
                    throw new Exception("Invalid email format");
                }
            }
            userService.createUser(userDto);
            return ResponseEntity.ok("Register user successfully");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

//    @PostMapping("/login")
//    public ResponseEntity<LoginResponse> login(@Valid @RequestBody UserLoginDto userLoginDto, HttpServletRequest request){
//        try{
//            String tokenLogin = userService.loginUser(userLoginDto.getPhoneNumber(), userLoginDto.getPassword());
//            Locale locale= localResolver.resolveLocale(request);
//            LoginResponse response= LoginResponse.builder()
//                                    .message(messageSource.getMessage(MessageKey.LOGIN_SUCCESS, null, locale))
//                    .token(tokenLogin)
//                    .build();
//            return ResponseEntity.ok(response);
//        }catch (Exception e){
//            return ResponseEntity.badRequest().body(LoginResponse.builder().message(e.getMessage()).build());
//        }
//
//    }
@Operation(
        summary = "Login "
)
    @PostMapping("/login")
    public ResponseEntity<ResponseObject> login(@Valid @RequestBody UserLoginDto userLoginDto, HttpServletRequest request){
        try{
            String tokenLogin = userService.loginUser(userLoginDto);

            String userAgent= request.getHeader("User-Agent");

            User user=userService.getUserDetailsFromToken(tokenLogin);

            Token jwt =tokenService.addToken(user,tokenLogin,userAgent);

            LoginResponse response= LoginResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKey.LOGIN_SUCCESS,webUtils))
                    .token(tokenLogin)
                    .tokenType(jwt.getTokenType())
                    .id(user.getId())
                    .username(user.getFullName())
                    .roles(user.getRole())
                    .build();
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Login successfully")
                    .data(response)
                    .status(HttpStatus.OK).build()
            );
        }catch (Exception e){
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .message(e.getMessage())
                            .data(null)
                            .status(HttpStatus.BAD_REQUEST).build()
            );
        }

    }
    private UserResponse mapToResponse(User user){
        return UserResponse.builder()
                .id(user.getId())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .fullName(user.getFullName())
                .isActive(user.getIsActive())
                .dateOfBirth(user.getDateOfBirth())
                .role(user.getRole())
                .build();
    }

    @Operation(
            summary = "Get user details by providing token"
    )

    @GetMapping("/details")
    public ResponseEntity<?> getUserDertails(@RequestHeader("Authorization") String token){
        try{
            String extractedToken= token.substring(7);
            User user =userService.getUserDetailsFromToken(extractedToken);

            return ResponseEntity.ok(mapToResponse(user));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
    @Operation(
            summary = "Get user information by providing id"
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserDetailsById(@PathVariable("id") Long id){
        try{

            UserResponse user =userService.getUserDetailsById(id);
            return ResponseEntity.ok(user);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
    @Operation(
            summary = "Delete user by admin"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserDetailsById(@PathVariable("id") Long id){
        try{

            userService.deleteUserByAdmin(id);
            return ResponseEntity.ok("Delete user successfully");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
    @Operation(
            summary = "Get list of users by amdin"
    )
    @GetMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getUsersByAdmin(@RequestHeader("Authorization") String token,
                                             @RequestParam(required = false, defaultValue = "") String keyword,
                                             @RequestParam(required = false, defaultValue = "0") int page,
                                             @RequestParam(required = false, defaultValue = "10") int limit){
        try{
            String extractedToken= token.substring(7);
            PageRequest pageRequest=PageRequest.of(page,limit, Sort.by("id").ascending());

            Page<UserResponse> users =userService.getAlluserAdmin(keyword,pageRequest);
            int totalPages=users.getTotalPages();
            long totalElements=users.getTotalElements();
            List<UserResponse> userResponses=users.getContent();
            return ResponseEntity.ok(UserListResponse.builder()
                            .users(userResponses)
                            .totalElements(totalElements)
                            .totalPages(totalPages)
                    .build());
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
    @Operation(
            summary = "Update user information by user or admin"
    )
    @PutMapping()
    public ResponseEntity<?> updateUserDertails(@RequestHeader("Authorization") String token,@RequestBody UserDto userDto){
        try{
            String extractedToken= token.substring(7);
            UserResponse user =userService.updateUserInfo(extractedToken,userDto);

            return ResponseEntity.ok(UserBaseResponse.builder()
                    .user(user)
                    .message("Updated user successfully")
                    .build());
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
}
