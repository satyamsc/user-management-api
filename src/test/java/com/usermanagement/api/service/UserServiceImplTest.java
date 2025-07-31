package com.usermanagement.api.service;

import com.usermanagement.api.dto.UserCreateRequest;
import com.usermanagement.api.dto.UserResponse;
import com.usermanagement.api.dto.UserUpdateRequest;
import com.usermanagement.api.exception.UserManagementException;
import com.usermanagement.api.model.User;
import com.usermanagement.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserCreateRequest createRequest;
    private UserUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        testUser = User.builder()
                .id(1L)
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .username("testuser")
                .password("password123")
                .createdAt(now)
                .updatedAt(now)
                .active(true)
                .build();

        createRequest = UserCreateRequest.builder()
                .firstName("New")
                .lastName("User")
                .email("new@example.com")
                .username("newuser")
                .password("newpassword123")
                .build();

        updateRequest = UserUpdateRequest.builder()
                .firstName("Updated")
                .lastName("User")
                .email("updated@example.com")
                .password("updatedpassword123")
                .active(true)
                .build();
    }

    @Test
    void shouldCreateUserSuccessfully() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserResponse response = userService.createUser(createRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(testUser.getId());
        verify(userRepository).existsByEmail(createRequest.getEmail());
        verify(userRepository).existsByUsername(createRequest.getUsername());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // When & Then
        assertThrows(UserManagementException.class, () -> userService.createUser(createRequest));
        verify(userRepository).existsByEmail(createRequest.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // When & Then
        assertThrows(UserManagementException.class, () -> userService.createUser(createRequest));
        verify(userRepository).existsByEmail(createRequest.getEmail());
        verify(userRepository).existsByUsername(createRequest.getUsername());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldGetUserByIdSuccessfully() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));

        // When
        UserResponse response = userService.getUserById(1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo(testUser.getUsername());
        verify(userRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundById() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserManagementException.class, () -> userService.getUserById(1L));
        verify(userRepository).findById(1L);
    }

    @Test
    void shouldGetAllUsersSuccessfully() {
        // Given
        User anotherUser = User.builder()
                .id(2L)
                .firstName("Another")
                .lastName("User")
                .email("another@example.com")
                .username("anotheruser")
                .password("password456")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .active(true)
                .build();

        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser, anotherUser));

        // When
        List<UserResponse> responses = userService.getAllUsers();

        // Then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getId()).isEqualTo(testUser.getId());
        assertThat(responses.get(1).getId()).isEqualTo(anotherUser.getId());
        verify(userRepository).findAll();
    }

    @Test
    void shouldUpdateUserSuccessfully() {
        // Given
        User updatedUser = User.builder()
                .id(1L)
                .firstName("Updated")
                .lastName("User")
                .email("updated@example.com")
                .username("testuser")
                .password("updatedpassword123")
                .createdAt(testUser.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .active(true)
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // When
        UserResponse response = userService.updateUser(1L, updateRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getFirstName()).isEqualTo(updateRequest.getFirstName());
        assertThat(response.getEmail()).isEqualTo(updateRequest.getEmail());
        verify(userRepository).findById(1L);
        verify(userRepository).findByEmail(updateRequest.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldDeleteUserSuccessfully() {
        // Given
        when(userRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(userRepository).deleteById(anyLong());

        // When
        userService.deleteUser(1L);

        // Then
        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentUser() {
        // Given
        when(userRepository.existsById(anyLong())).thenReturn(false);

        // When & Then
        assertThrows(UserManagementException.class, () -> userService.deleteUser(1L));
        verify(userRepository).existsById(1L);
        verify(userRepository, never()).deleteById(anyLong());
    }
}