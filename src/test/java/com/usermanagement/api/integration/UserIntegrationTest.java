package com.usermanagement.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.usermanagement.api.dto.UserCreateRequest;
import com.usermanagement.api.dto.UserResponse;
import com.usermanagement.api.dto.UserUpdateRequest;
import com.usermanagement.api.model.User;
import com.usermanagement.api.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void shouldCreateAndRetrieveUser() throws Exception {
        // Given
        UserCreateRequest createRequest = UserCreateRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .username("johndoe")
                .password("password123")
                .build();

        // When - Create user
        MvcResult result = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")))
                .andExpect(jsonPath("$.username", is("johndoe")))
                .andReturn();

        // Extract user ID from response
        UserResponse createdUser = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserResponse.class);
        Long userId = createdUser.getId();

        // Then - Verify user exists in database
        Optional<User> savedUser = userRepository.findById(userId);
        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getFirstName()).isEqualTo("John");
        assertThat(savedUser.get().getEmail()).isEqualTo("john.doe@example.com");

        // And - Retrieve user by ID
        mockMvc.perform(get("/api/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId.intValue())))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")))
                .andExpect(jsonPath("$.username", is("johndoe")));
    }

    @Test
    void shouldUpdateUser() throws Exception {
        // Given - Create a user
        UserCreateRequest createRequest = UserCreateRequest.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .username("janesmith")
                .password("password123")
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        UserResponse createdUser = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), UserResponse.class);
        Long userId = createdUser.getId();

        // When - Update the user
        UserUpdateRequest updateRequest = UserUpdateRequest.builder()
                .firstName("Jane Updated")
                .lastName("Smith Updated")
                .email("jane.updated@example.com")
                .build();

        mockMvc.perform(put("/api/users/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("Jane Updated")))
                .andExpect(jsonPath("$.lastName", is("Smith Updated")))
                .andExpect(jsonPath("$.email", is("jane.updated@example.com")));

        // Then - Verify user is updated in database
        Optional<User> updatedUser = userRepository.findById(userId);
        assertThat(updatedUser).isPresent();
        assertThat(updatedUser.get().getFirstName()).isEqualTo("Jane Updated");
        assertThat(updatedUser.get().getLastName()).isEqualTo("Smith Updated");
        assertThat(updatedUser.get().getEmail()).isEqualTo("jane.updated@example.com");
    }

    @Test
    void shouldDeleteUser() throws Exception {
        // Given - Create a user
        UserCreateRequest createRequest = UserCreateRequest.builder()
                .firstName("Delete")
                .lastName("Me")
                .email("delete.me@example.com")
                .username("deleteme")
                .password("password123")
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        UserResponse createdUser = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), UserResponse.class);
        Long userId = createdUser.getId();

        // When - Delete the user
        mockMvc.perform(delete("/api/users/" + userId))
                .andExpect(status().isNoContent());

        // Then - Verify user is deleted from database
        Optional<User> deletedUser = userRepository.findById(userId);
        assertThat(deletedUser).isEmpty();

        // And - Verify 404 when trying to get deleted user
        mockMvc.perform(get("/api/users/" + userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnConflictWhenCreatingUserWithExistingEmail() throws Exception {
        // Given - Create a user
        UserCreateRequest firstUser = UserCreateRequest.builder()
                .firstName("First")
                .lastName("User")
                .email("duplicate@example.com")
                .username("firstuser")
                .password("password123")
                .build();

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firstUser)))
                .andExpect(status().isCreated());

        // When - Try to create another user with the same email
        UserCreateRequest duplicateEmailUser = UserCreateRequest.builder()
                .firstName("Second")
                .lastName("User")
                .email("duplicate@example.com") // Same email
                .username("seconduser")
                .password("password456")
                .build();

        // Then - Expect conflict response
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateEmailUser)))
                .andExpect(status().isConflict());
    }
}