package com.blackcoffee.shopapp.services.impl;

import com.blackcoffee.shopapp.dto.ResetPasswordDto;
import com.blackcoffee.shopapp.dto.UserDto;
import com.blackcoffee.shopapp.exception.DataNotFoundException;
import com.blackcoffee.shopapp.exception.InvalidParamsException;
import com.blackcoffee.shopapp.exception.PermissionDenyException;
import com.blackcoffee.shopapp.model.Role;
import com.blackcoffee.shopapp.model.User;
import com.blackcoffee.shopapp.repository.RoleRepository;
import com.blackcoffee.shopapp.repository.UserRepository;
import com.blackcoffee.shopapp.response.UserResponse;
import com.blackcoffee.shopapp.services.BaseRedisService;
import com.blackcoffee.shopapp.services.EmailService;
import com.blackcoffee.shopapp.services.UserService;
import com.blackcoffee.shopapp.utils.JwtTokenUtils;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;
    private final EmailService emailService;
    private final BaseRedisService baseRedisService;
    private final AuthenticationManager authenticationManager;
    @Override
    public User createUser(UserDto userDto) throws Exception {
        //check phone number exist or not
        Boolean check= userRepository.existsByPhoneNumber(userDto.getPhoneNumber());
        Boolean checkEmail= userRepository.existsByEmail(userDto.getPhoneNumber());
        if(check){
            throw new DataIntegrityViolationException("Phone number already exists");
        }
        if(checkEmail){
            throw new DataIntegrityViolationException("Email already exists");
        }
        Role role= roleRepository.findById(userDto.getRoleId()).orElseThrow(()-> new DataNotFoundException("Role is invalid"));

        if(role.getName().toUpperCase().equals("ADMIN")){
            throw new PermissionDenyException("Cannot create account as an admin");
        }
        User user=User.builder()
                .fullName(userDto.getFullName())
                .phoneNumber(userDto.getPhoneNumber())
                .email(userDto.getEmail())
                .address(userDto.getAddress())
                .password(userDto.getPassword())
                .dateOfBirth(userDto.getDateOfBirth())
                .facebookAccountId(userDto.getFacebookAccountId())
                .googleAccountId(userDto.getGoogleAccountId())
                .build();


        user.setRole(role);
        if(userDto.getFacebookAccountId()==0 && userDto.getGoogleAccountId()==0){
            String password= userDto.getPassword();
            String encodePassword= passwordEncoder.encode(password);
            user.setPassword(encodePassword);
        }
        return userRepository.save(user);
    }

    @Override
    public Boolean existByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public void checkTokenValid(String otp, String email) {
        Object otpFound=baseRedisService.get(email);
        if(!otpFound.equals(otp) && otpFound!=null){
            throw new DataNotFoundException("Invalid OTP. Please check your email box.");
        }
    }

    @Override
    public void requestResetPassword(String email) throws MessagingException {
        try{
            User user=userRepository.findByEmail(email).orElseThrow(()-> new DataNotFoundException("User not found with email"));

            Long otp=otpGenerator();

            StringBuilder userKey= new StringBuilder();
            userKey.append(user.getEmail());
            baseRedisService.set(userKey.toString(),otp.toString());
            baseRedisService.setTimeToLive(userKey.toString(),30);
            emailService.sendEmailResetPassword(user.getEmail(), otp);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }


    }

    @Override
    @Transactional
    public void resetPassword(String newPassword, String email) {
        Object otpFound=baseRedisService.get(email);
        if(otpFound==null){
            throw new DataNotFoundException("Invalid OTP, please check your email inbox.");
        }
        baseRedisService.delete(email);
        User user=userRepository.findByEmail(email).orElseThrow(()-> new DataNotFoundException("User not found with email"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

    }

    @Override
    @Transactional
    public void changePassword(Long userId, String newPassword) {
        User user=userRepository.findById(userId).orElseThrow(()-> new DataNotFoundException("User not found with email"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private Long otpGenerator(){
        Random random= new Random();
        return random.nextLong(100_000,999_999);
    }


    @Override
    public String loginUser(String phoneNumber, String password, Long roleId) throws InvalidParamsException {
        Optional<User> existingUser =userRepository.findByPhoneNumber(phoneNumber);
        if(existingUser.isEmpty()){
            throw new DataNotFoundException("Invalid phone number or password");
        }
        if(existingUser.get().getFacebookAccountId()==0 && (existingUser.get().getGoogleAccountId()==0)){
            if(!passwordEncoder.matches(password, existingUser.get().getPassword())){
                throw new BadCredentialsException("Wrong password or phone number");
            }

        }
        Optional<Role> roleFound=roleRepository.findById(roleId);
        if(roleFound.isEmpty() || !roleId.equals(existingUser.get().getRole().getId())){
            throw new BadCredentialsException("Password or phone number does not exist.");
        }
        UsernamePasswordAuthenticationToken authenticationToken= new UsernamePasswordAuthenticationToken(phoneNumber,password);

        authenticationManager.authenticate(authenticationToken);
        return jwtTokenUtils.generateToken(existingUser.get());

    }

    @Override
    public UserResponse getUserDetailsFromToken(String token) throws Exception {

        if(jwtTokenUtils.isTokenExpired(token)){
            throw new Exception("Token is expired");
        }
        String phoneNumber= jwtTokenUtils.extractPhoneNumber(token);
        User user= userRepository.findByPhoneNumber(phoneNumber).orElseThrow(()-> new DataNotFoundException("User not found"));


        return mapToResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUserInfo(String token, UserDto userDto) throws PermissionDenyException {
        String phoneNumber= jwtTokenUtils.extractPhoneNumber(token);
        User user= userRepository.findByPhoneNumber(phoneNumber).orElseThrow(()-> new DataNotFoundException("User not found"));
        User foundUser=userRepository.findByPhoneNumber(userDto.getPhoneNumber()).orElseThrow(()-> new DataNotFoundException("User not found"));
        if(user.getId()!= foundUser.getId()){
            throw new PermissionDenyException("Cannot update user.Invalid user permission");
        }
        user.setFullName(userDto.getFullName());
        user.setAddress(userDto.getAddress());
        user.setDateOfBirth(userDto.getDateOfBirth());
        User updatedUser=userRepository.save(user);
        return mapToResponse(updatedUser);
    }

    @Override
    public Page<UserResponse> getAlluserAdmin(String keyword, Pageable pageable) {
        return userRepository.searchUser(keyword, pageable).map(user->{
            UserResponse userResponse=UserResponse.builder()
                    .address(user.getAddress())
                    .id(user.getId())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .phoneNumber(user.getPhoneNumber())
                    .isActive(user.getIsActive())
                    .dateOfBirth(user.getDateOfBirth())
                    .role(user.getRole())
                    .build();
            return userResponse;
        });



    }

    @Override
    public UserResponse updateRole(Long roleId, Long id) throws Exception {

        User user= userRepository.findById(id).orElseThrow(()-> new DataNotFoundException("User not found"));
        Role role= roleRepository.findById(roleId).orElseThrow(()-> new DataNotFoundException("Role is invalid"));
        user.setRole(role);
        User userUpdated=userRepository.save(user);
        return mapToResponse(userUpdated);
    }

    @Override
    public UserResponse getUserDetailsById(Long id) {
        User user= userRepository.findById(id).orElseThrow(()-> new DataNotFoundException("User not found"));

        return mapToResponse(user);
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
}
