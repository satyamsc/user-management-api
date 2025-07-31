package com.usermanagement.api.service;

import com.usermanagement.api.dto.UserCreateRequest;
import com.usermanagement.api.dto.UserResponse;
import com.usermanagement.api.dto.UserUpdateRequest;
import com.usermanagement.api.exception.UserManagementException;
import com.usermanagement.api.model.User;
import com.usermanagement.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw UserManagementException.conflict("Email already in use: " + request.getEmail());
        }

        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw UserManagementException.conflict("Username already in use: " + request.getUsername());
        }

        // Create new user
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .username(request.getUsername())
                .password(request.getPassword()) // In a real application, password should be encrypted
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .active(true)
                .build();

        User savedUser = userRepository.save(user);
        return mapToUserResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> UserManagementException.notFound("User not found with id: " + id));
        return mapToUserResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> UserManagementException.notFound("User not found with username: " + username));
        return mapToUserResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> UserManagementException.notFound("User not found with email: " + email));
        return mapToUserResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> UserManagementException.notFound("User not found with id: " + id));

        // Update email if provided and not already in use by another user
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            userRepository.findByEmail(request.getEmail())
                    .ifPresent(existingUser -> {
                        if (!existingUser.getId().equals(id)) {
                            throw UserManagementException.conflict("Email already in use: " + request.getEmail());
                        }
                    });
            user.setEmail(request.getEmail());
        }

        // Update other fields if provided
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }

        if (request.getPassword() != null) {
            user.setPassword(request.getPassword()); // In a real application, password should be encrypted
        }

        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }

        user.setUpdatedAt(LocalDateTime.now());
        User updatedUser = userRepository.save(user);
        return mapToUserResponse(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw UserManagementException.notFound("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .username(user.getUsername())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .active(user.isActive())
                .build();
    }
}