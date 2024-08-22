package com.blackcoffee.shopapp.repository;

import com.blackcoffee.shopapp.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
