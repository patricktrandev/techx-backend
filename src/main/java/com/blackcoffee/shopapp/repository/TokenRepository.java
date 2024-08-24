package com.blackcoffee.shopapp.repository;

import com.blackcoffee.shopapp.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TokenRepository extends JpaRepository<Token, Long> {
    List<Token> findByUserId(Long id);
}
