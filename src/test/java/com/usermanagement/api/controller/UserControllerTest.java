package com.usermanagement.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.usermanagement.api.dto.UserCreateRequest;
import com.usermanagement.api.dto.UserResponse;
import com.usermanagement.api.dto.UserUpdateRequest;
import com.usermanagement.api.exception.UserManagementException;
import com.usermanagement.api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserResponse testUserResponse;
    private UserCreateRequest createRequest;
    private UserUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        testUserResponse = UserResponse.builder()
                .id(1L)
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .username("testuser")
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
    void shouldCreateUserSuccessfully() throws Exception {
        // Given
        when(userService.createUser(any(UserCreateRequest.class))).thenReturn(testUserResponse);

        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("Test")))
                .andExpect(jsonPath("$.lastName", is("User")))
                .andExpect(jsonPath("$.email", is("test@example.com")))
                .andExpect(jsonPath("$.username", is("testuser")));

        verify(userService).createUser(any(UserCreateRequest.class));
    }

    @Test
    void shouldReturnUserById() throws Exception {
        // Given
        when(userService.getUserById(anyLong())).thenReturn(testUserResponse);

        // When & Then
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("Test")))
                .andExpect(jsonPath("$.lastName", is("User")))
                .andExpect(jsonPath("$.email", is("test@example.com")))
                .andExpect(jsonPath("$.username", is("testuser")));

        verify(userService).getUserById(1L);
    }

    @Test
    void shouldReturnUserByUsername() throws Exception {
        // Given
        when(userService.getUserByUsername(anyString())).thenReturn(testUserResponse);

        // When & Then
        mockMvc.perform(get("/api/users/username/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("Test")))
                .andExpect(jsonPath("$.lastName", is("User")))
                .andExpect(jsonPath("$.email", is("test@example.com")))
                .andExpect(jsonPath("$.username", is("testuser")));

        verify(userService).getUserByUsername("testuser");
    }

    @Test
    void shouldReturnUserByEmail() throws Exception {
        // Given
        when(userService.getUserByEmail(anyString())).thenReturn(testUserResponse);

        // When & Then
        mockMvc.perform(get("/api/users/email/test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("Test")))
                .andExpect(jsonPath("$.lastName", is("User")))
                .andExpect(jsonPath("$.email", is("test@example.com")))
                .andExpect(jsonPath("$.username", is("testuser")));

        verify(userService).getUserByEmail("test@example.com");
    }

    @Test
    void shouldReturnAllUsers() throws Exception {
        // Given
        UserResponse anotherUserResponse = UserResponse.builder()
                .id(2L)
                .firstName("Another")
                .lastName("User")
                .email("another@example.com")
                .username("anotheruser")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .active(true)
                .build();

        List<UserResponse> users = Arrays.asList(testUserResponse, anotherUserResponse);
        when(userService.getAllUsers()).thenReturn(users);

        // When & Then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].username", is("testuser")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].username", is("anotheruser")));

        verify(userService).getAllUsers();
    }

    @Test
    void shouldUpdateUserSuccessfully() throws Exception {
        // Given
        UserResponse updatedUserResponse = UserResponse.builder()
                .id(1L)
                .firstName("Updated")
                .lastName("User")
                .email("updated@example.com")
                .username("testuser")
                .createdAt(testUserResponse.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .active(true)
                .build();

        when(userService.updateUser(anyLong(), any(UserUpdateRequest.class))).thenReturn(updatedUserResponse);

        // When & Then
        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("Updated")))
                .andExpect(jsonPath("$.lastName", is("User")))
                .andExpect(jsonPath("$.email", is("updated@example.com")))
                .andExpect(jsonPath("$.username", is("testuser")));

        verify(userService).updateUser(eq(1L), any(UserUpdateRequest.class));
    }

    @Test
    void shouldDeleteUserSuccessfully() throws Exception {
        // Given
        doNothing().when(userService).deleteUser(anyLong());

        // When & Then
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(1L);
    }

    @Test
    void shouldHandleUserNotFoundException() throws Exception {
        // Given
        when(userService.getUserById(anyLong()))
                .thenThrow(UserManagementException.notFound("User not found with id: 1"));

        // When & Then
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(HttpStatus.NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is("User not found with id: 1")));

        verify(userService).getUserById(1L);
    }

    @Test
    void shouldHandleValidationErrors() throws Exception {
        // Given
        UserCreateRequest invalidRequest = UserCreateRequest.builder()
                .firstName("")
                .lastName("")
                .email("invalid-email")
                .username("usr")
                .password("short")
                .build();

        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(UserCreateRequest.class));
    }
}