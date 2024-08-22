package com.blackcoffee.shopapp.services.impl;

import com.blackcoffee.shopapp.model.Role;
import com.blackcoffee.shopapp.repository.RoleRepository;
import com.blackcoffee.shopapp.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
}
