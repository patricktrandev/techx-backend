package com.blackcoffee.shopapp.controller;

import com.blackcoffee.shopapp.dto.UserRoleDto;
import com.blackcoffee.shopapp.model.Role;
import com.blackcoffee.shopapp.response.UserResponse;
import com.blackcoffee.shopapp.services.RoleService;
import com.blackcoffee.shopapp.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/roles")
@RequiredArgsConstructor
@Tag(name = "CRUD REST API for Role Resource")
public class RoleController {
    private final RoleService roleService;
    private final UserService userService;
    @GetMapping
    @Operation(
            summary = "Get list of roles"
    )
    public ResponseEntity<List<Role>> getAllRoles(){
        return ResponseEntity.ok(roleService.getAllRoles());
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(
            summary = "Update role of user by admin"
    )
    public ResponseEntity<?> updateRole(@Valid @RequestBody UserRoleDto userRoleDto, BindingResult bindingResult,
                                        @PathVariable("id") Long id){

        try{
            if(bindingResult.hasErrors()){
                List<String> errors =bindingResult.getFieldErrors().stream().map(e->e.getDefaultMessage()).toList();
                return ResponseEntity.badRequest().body(errors);
            }
            UserResponse userResponse=userService.updateRole(userRoleDto.getRole(),id);
            return ResponseEntity.ok(userResponse);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
