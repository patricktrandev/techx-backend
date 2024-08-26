package com.blackcoffee.shopapp.services.impl;

import com.blackcoffee.shopapp.exception.DataNotFoundException;
import com.blackcoffee.shopapp.model.Token;
import com.blackcoffee.shopapp.model.User;
import com.blackcoffee.shopapp.repository.TokenRepository;
import com.blackcoffee.shopapp.response.UserResponse;
import com.blackcoffee.shopapp.services.TokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private final TokenRepository tokenRepository;
    @Value("${jwt.expiration}")
    private int expiration;


    @Override
    @Transactional
    public Token addToken(User user, String token,String deviceType) {
        List<Token> userTokens = tokenRepository.findByUserId(user.getId());
        int tokenCount = userTokens.size();

        if (tokenCount >= 4) {
            //boolean hasNonMobileToken = userTokens.stream().allMatch(Token::isMobile);

            List<Token> tokenList=tokenRepository.findByUserId(user.getId());
            tokenRepository.delete(tokenList.get(0));
        }
        long expirationInSeconds = expiration;
        LocalDateTime expirationDateTime = LocalDateTime.now().plusSeconds(expiration);

        Token newToken = Token.builder()
                .user(user)
                .token(token)
                .revoked(0)
                .expired(0)
                .tokenType("Bearer")
                .expirationDate(expirationDateTime)
                .deviceType(deviceType)
                .build();

//        newToken.setRefreshToken(UUID.randomUUID().toString());
//        newToken.setRefreshExpirationDate(LocalDateTime.now().plusSeconds(expirationRefreshToken));

        return tokenRepository.save(newToken);

    }
    private User mapToModel(UserResponse user){
        return User.builder()
                .id(user.getId())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .fullName(user.getFullName())
                .isActive(user.getIsActive())
                .dateOfBirth(user.getDateOfBirth())
                .role(user.getRole())
                .build();
    }
    private String identifyDevice(String userAgent) {
        // A basic example of device identification
        if (userAgent.contains("Mobile")) {
            return "Mobile";
        } else if (userAgent.contains("Tablet")) {
            return "Tablet";
        } else if (userAgent.contains("Windows") || userAgent.contains("Macintosh")) {
            return "Desktop";

        }else if (userAgent.contains("Iphone") ) {
            return "Iphone";
        }else if (userAgent.contains("Ipad") ) {
            return "Ipad";
        }
        else if (userAgent.contains("Postman") ) {
            return "Postman";
        }
        else {
            return "Unknown";
        }
    }
}
