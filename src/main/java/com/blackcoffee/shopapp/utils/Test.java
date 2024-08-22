package com.blackcoffee.shopapp.utils;

import io.jsonwebtoken.io.Encoders;
import org.springframework.security.web.SecurityFilterChain;

import java.beans.Encoder;
import java.security.SecureRandom;

public class Test {

    public static void main(String[] args) {
        System.out.println(generateSecretKey());
    }
    static String generateSecretKey(){
        SecureRandom random= new SecureRandom();
        byte[] keyBytes=new byte[32];
        random.nextBytes(keyBytes);
        String secret= Encoders.BASE64.encode(keyBytes);
        return secret;
    }
}
