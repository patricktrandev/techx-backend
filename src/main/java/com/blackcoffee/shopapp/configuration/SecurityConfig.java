package com.blackcoffee.shopapp.configuration;

import com.blackcoffee.shopapp.model.User;
import com.blackcoffee.shopapp.repository.UserRepository;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Configuration
@RequiredArgsConstructor

public class SecurityConfig {
    private final UserRepository userRepository;
    //user details object

    @Bean
    public UserDetailsService userDetailsService() {
        return subject -> {
            // Attempt to find user by phone number
            Optional<User> userByPhoneNumber = userRepository.findByPhoneNumber(subject);
            if (userByPhoneNumber.isPresent()) {
                return userByPhoneNumber.get();
            }
            // If user not found by phone number, attempt to find by email
            Optional<User> userByEmail = userRepository.findByEmail(subject);
            if (userByEmail.isPresent()) {
                return userByEmail.get();
            }
            // If user not found by either phone number or email, throw UsernameNotFoundException
            throw new UsernameNotFoundException("User not found with subject: " + subject);
        };
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider= new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
