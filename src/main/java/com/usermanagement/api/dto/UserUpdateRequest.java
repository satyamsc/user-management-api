package com.usermanagement.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {

    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    @Email(message = "Email should be valid")
    @Size(max = 100)
    private String email;

    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;
    
    private Boolean active;
}