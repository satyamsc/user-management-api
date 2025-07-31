package com.usermanagement.api.service;

import com.usermanagement.api.dto.UserCreateRequest;
import com.usermanagement.api.dto.UserResponse;
import com.usermanagement.api.dto.UserUpdateRequest;

import java.util.List;

public interface UserService {

    UserResponse createUser(UserCreateRequest request);
    
    UserResponse getUserById(Long id);
    
    UserResponse getUserByUsername(String username);
    
    UserResponse getUserByEmail(String email);
    
    List<UserResponse> getAllUsers();
    
    UserResponse updateUser(Long id, UserUpdateRequest request);
    
    void deleteUser(Long id);
}