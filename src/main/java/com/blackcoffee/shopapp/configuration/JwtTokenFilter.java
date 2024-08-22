package com.blackcoffee.shopapp.configuration;

import com.blackcoffee.shopapp.model.User;
import com.blackcoffee.shopapp.utils.JwtTokenUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.NonNull;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final JwtTokenUtils jwtTokenUtils;
    @Value("${api.prefix}")
    private String apiPrefix;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,@NonNull HttpServletResponse response,@NonNull FilterChain filterChain) throws ServletException, IOException {
        //filterChain.doFilter(request,response);//enable bypass
        try{
            if(isByPassToken(request)){
                filterChain.doFilter(request,response);
                return;
            }
            final String authHeader= request.getHeader("Authorization");
            if(authHeader!=null && !authHeader.startsWith("Bearer ")){
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Unauthorized");
                return;
            }
            final String token= authHeader.substring(7);
            final String phoneNumber=jwtTokenUtils.extractPhoneNumber(token);
            if(phoneNumber!= null && SecurityContextHolder.getContext().getAuthentication()==null){
                User userDetails= (User)  userDetailsService.loadUserByUsername(phoneNumber);
                if(jwtTokenUtils.validateToken(token, userDetails)){
                    UsernamePasswordAuthenticationToken authenticationToken= new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }

            filterChain.doFilter(request,response);
        }catch (Exception e){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Unauthorized");
        }

    }

    private boolean isByPassToken(@NonNull HttpServletRequest request){
        final List<Pair<String, String>> byPassTokens= Arrays.asList(
                Pair.of(apiPrefix+"/products","GET"),
                Pair.of(apiPrefix+"/roles","GET"),

                Pair.of(apiPrefix+"/categories","GET"),
                Pair.of(apiPrefix+"/users/register","POST"),
                Pair.of(apiPrefix+"/users/login","POST")

        );
        String requestPath=request.getServletPath();
        String requestMethod=request.getMethod();
        if(requestMethod.equals("GET") && requestPath.equals(String.format("%s/orders",apiPrefix))){
            return true;
        }


        for(Pair<String, String> byPassToken: byPassTokens){
            if(request.getServletPath().contains(byPassToken.getFirst())&& request.getMethod().equals(byPassToken.getSecond())){
                return true;
            }
        }

        return false;
    }
}
