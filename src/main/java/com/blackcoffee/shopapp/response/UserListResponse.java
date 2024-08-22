package com.blackcoffee.shopapp.response;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserListResponse {
    private List<UserResponse> users;
    private int totalPages;
    private long totalElements;
}
