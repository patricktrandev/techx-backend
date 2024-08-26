package com.blackcoffee.shopapp.configuration;

import com.blackcoffee.shopapp.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;

    @Value("${api.prefix}")
    private String apiPrefix;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request-> {
                    request
                            .requestMatchers(
                                    String.format("%s/users/register",apiPrefix),
                                    String.format("%s/users/login",apiPrefix),
                                    String.format("%s/users/reset/send-email",apiPrefix),
                                    String.format("%s/users/reset-password",apiPrefix),
                                    String.format("%s/users/token-valid",apiPrefix),
                                    String.format("%s/healthcheck/**",apiPrefix)
                            ).permitAll()
                            .requestMatchers(
                                    HttpMethod.GET,String.format("%s/coupons/calculate",apiPrefix)
                            ).permitAll()
                            .requestMatchers("/swagger-ui/**",
                                    "/swagger-resources/*",
                                    "/v3/api-docs/**")
                            .permitAll()

                            .requestMatchers(
                                    HttpMethod.GET,String.format("%s/products/**",apiPrefix)
                            ).permitAll()
                            .requestMatchers(
                                    HttpMethod.GET,String.format("%s/roles/**",apiPrefix)
                            ).permitAll()
                            .requestMatchers(
                                    HttpMethod.GET,String.format("%s/products/images/**",apiPrefix)
                            ).permitAll()
                            .requestMatchers(
                                    HttpMethod.GET,String.format("%s/categories/**",apiPrefix)
                            ).permitAll()
                            .requestMatchers(
                                    HttpMethod.PUT,String.format("%s/users/**",apiPrefix)
                            ).hasAnyRole("USER", "ADMIN")
                            .requestMatchers(
                                    HttpMethod.GET,String.format("%s/users/**",apiPrefix)
                            ).hasAnyRole("USER", "ADMIN")
                            .requestMatchers(
                                    HttpMethod.DELETE,String.format("%s/users/**",apiPrefix)
                            ).hasAnyRole( "ADMIN")
                            .requestMatchers(
                                    HttpMethod.PUT,String.format("%s/roles/**",apiPrefix)
                            ).hasAnyRole( "ADMIN")
                            .requestMatchers(
                                    HttpMethod.POST,String.format("%s/categories/**",apiPrefix)
                            ).hasAnyRole( "ADMIN")
                            .requestMatchers(
                                    HttpMethod.PUT,String.format("%s/categories/**",apiPrefix)
                            ).hasAnyRole( "ADMIN")
                            .requestMatchers(
                                    HttpMethod.DELETE,String.format("%s/categories/**",apiPrefix)
                            ).hasAnyRole( "ADMIN")
                            .requestMatchers(
                                    HttpMethod.POST,String.format("%s/products/**",apiPrefix)
                            ).hasAnyRole( "ADMIN")
                            .requestMatchers(
                                    HttpMethod.PUT,String.format("%s/products/**",apiPrefix)
                            ).hasAnyRole( "ADMIN")
                            .requestMatchers(
                                    HttpMethod.DELETE,String.format("%s/products/**",apiPrefix)
                            ).hasAnyRole( "ADMIN")
                            .requestMatchers(
                                    HttpMethod.GET,String.format("%s/order_details/**",apiPrefix)
                            ).hasAnyRole("USER", "ADMIN")
                            .requestMatchers(
                                    HttpMethod.POST,String.format("%s/order_details/**",apiPrefix)
                            ).hasAnyRole( "ADMIN")
                            .requestMatchers(
                                    HttpMethod.PUT,String.format("%s/order_details/**",apiPrefix)
                            ).hasAnyRole( "ADMIN")
                            .requestMatchers(
                                    HttpMethod.DELETE,String.format("%s/order_details/**",apiPrefix)
                            ).hasAnyRole( "ADMIN")

                            .requestMatchers(
                                    HttpMethod.POST,String.format("%s/orders/**",apiPrefix)
                            ).hasAnyRole("USER")
                            .requestMatchers(
                                    HttpMethod.PUT,String.format("%s/orders/**",apiPrefix)
                            ).hasRole("ADMIN")
                            .requestMatchers(
                                    HttpMethod.GET,String.format("%s/orders/**",apiPrefix)
                            ).hasAnyRole( "USER", "ADMIN")
                            .requestMatchers(
                                    HttpMethod.DELETE,String.format("%s/orders/**",apiPrefix)
                            ).hasRole("ADMIN")
                            .requestMatchers(
                                    HttpMethod.GET,String.format("%s/coupons/**",apiPrefix)
                            ).hasAnyRole( "ADMIN")
                            .requestMatchers(
                                    HttpMethod.DELETE,String.format("%s/coupons/**",apiPrefix)
                            ).hasAnyRole( "ADMIN")
                            .requestMatchers(
                                    HttpMethod.POST,String.format("%s/coupons/**",apiPrefix)
                            ).hasAnyRole( "ADMIN")
                            .anyRequest().authenticated();
                })
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
