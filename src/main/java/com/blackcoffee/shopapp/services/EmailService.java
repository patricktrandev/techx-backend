package com.blackcoffee.shopapp.services;

import com.blackcoffee.shopapp.response.OrderResponse;
import jakarta.mail.MessagingException;

import java.util.List;

public interface EmailService {
    void sendEmailOrderSuccessfully(String userEmail, List<OrderResponse> orderResponseList) throws MessagingException;
    void sendEmailResetPassword(String userEmail, Long otp)throws MessagingException;
}
