package com.blackcoffee.shopapp.repository;

import com.blackcoffee.shopapp.model.Product;
import com.blackcoffee.shopapp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByPhoneNumber(String phoneNumber);
    Boolean existsByEmail(String email);
    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByEmail(String email);
    @Query("SELECT u FROM User u where " +
            "(:keyword IS NULL OR :keyword= '' OR u.email like %:keyword% or u.fullName like %:keyword% or u.phoneNumber like %:keyword% or u.address like %:keyword%)")
    Page<User> searchUser(@Param("keyword")String keyword, Pageable pageable);
}
