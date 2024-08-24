package com.blackcoffee.shopapp.services.impl;

import com.blackcoffee.shopapp.response.OrderResponse;
import com.blackcoffee.shopapp.response.ProductResponse;
import com.blackcoffee.shopapp.services.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String sender;
    @Override
    public void sendEmailOrderSuccessfully(String userEmail, List<OrderResponse> orderResponseList) throws MessagingException {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper= new MimeMessageHelper(mimeMessage,"utf-8");

        String subject="Your order ";
        String text="Click the link to join team project \n";
        helper.setFrom(sender);
        helper.setTo(userEmail);
        helper.setSubject(subject);
        helper.setText(text, true);

        try{
            javaMailSender.send(mimeMessage);
        }catch(MailSendException e){
            System.out.println(e.getMessage());
            throw new MailSendException("Failed to send email");
        }
    }

    @Override
    public void sendEmailResetPassword(String userEmail, Long otp) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper= new MimeMessageHelper(mimeMessage,"utf-8");

        String subject="Reset Password | TechX";
        String text="Your otp: \n"+otp;
        helper.setFrom(sender);
        helper.setTo(userEmail);
        helper.setSubject(subject);
        helper.setText(text, true);

        try{
            javaMailSender.send(mimeMessage);
        }catch(MailSendException e){
            System.out.println(e.getMessage());
            throw new MailSendException("Failed to send email");
        }
    }
}
