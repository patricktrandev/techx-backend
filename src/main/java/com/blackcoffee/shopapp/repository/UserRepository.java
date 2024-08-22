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
    Optional<User> findByPhoneNumber(String phoneNumber);
    @Query("SELECT u FROM User u where " +
            "(:keyword IS NULL OR :keyword= '' or u.fullName like %:keyword% or u.phoneNumber like %:keyword% or u.address like %:keyword%)")
    Page<User> searchUser(@Param("keyword")String keyword, Pageable pageable);
}
