package com.blackcoffee.shopapp.controller;

import com.blackcoffee.shopapp.dto.UserDto;
import com.blackcoffee.shopapp.dto.UserLoginDto;
import com.blackcoffee.shopapp.model.User;
import com.blackcoffee.shopapp.response.*;
import com.blackcoffee.shopapp.services.UserService;
import com.blackcoffee.shopapp.utils.LocalizationUtils;
import com.blackcoffee.shopapp.utils.MessageKey;
import com.blackcoffee.shopapp.utils.WebUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.hibernate.sql.Update;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final MessageSource messageSource;
    private final LocaleResolver localResolver;
    private final LocalizationUtils localizationUtils;
    private final WebUtils webUtils;


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDto userDto, BindingResult bindingResult){

        try{
            if(bindingResult.hasErrors()){
                List<String> errors =bindingResult.getFieldErrors().stream().map(e->e.getDefaultMessage()).toList();
                return ResponseEntity.badRequest().body(errors);
            }
            if(!(userDto.getPassword().equals(userDto.getConfirmPassword()))){
                return ResponseEntity.badRequest().body("Password does not match");
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

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody UserLoginDto userLoginDto){
        try{
            String tokenLogin = userService.loginUser(userLoginDto.getPhoneNumber(), userLoginDto.getPassword(), userLoginDto.getRoleId());

            LoginResponse response= LoginResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKey.LOGIN_SUCCESS,webUtils))
                    .token(tokenLogin)

                    .build();
            return ResponseEntity.ok(response);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(LoginResponse.builder().message(localizationUtils.getLocalizedMessage(MessageKey.LOGIN_FAILED,e.getMessage())).build());
        }

    }
    @GetMapping("/details")
    public ResponseEntity<?> getUserDertails(@RequestHeader("Authorization") String token){
        try{
            String extractedToken= token.substring(7);
            UserResponse user =userService.getUserDetailsFromToken(extractedToken);
            return ResponseEntity.ok(user);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
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
