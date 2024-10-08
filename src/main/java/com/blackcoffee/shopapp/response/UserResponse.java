package com.blackcoffee.shopapp.response;

import com.blackcoffee.shopapp.model.Role;
import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private int isActive;
    private LocalDate dateOfBirth;
    private Role role;


}
