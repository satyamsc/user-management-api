package com.usermanagement.api.repository;

import com.usermanagement.api.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void shouldSaveUser() {
        // Given
        User user = createTestUser();

        // When
        User savedUser = userRepository.save(user);

        // Then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("testuser");
    }

    @Test
    public void shouldFindUserByUsername() {
        // Given
        User user = createTestUser();
        userRepository.save(user);

        // When
        Optional<User> foundUser = userRepository.findByUsername("testuser");

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    public void shouldFindUserByEmail() {
        // Given
        User user = createTestUser();
        userRepository.save(user);

        // When
        Optional<User> foundUser = userRepository.findByEmail("test@example.com");

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    public void shouldCheckIfUserExistsByEmail() {
        // Given
        User user = createTestUser();
        userRepository.save(user);

        // When
        boolean exists = userRepository.existsByEmail("test@example.com");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    public void shouldCheckIfUserExistsByUsername() {
        // Given
        User user = createTestUser();
        userRepository.save(user);

        // When
        boolean exists = userRepository.existsByUsername("testuser");

        // Then
        assertThat(exists).isTrue();
    }

    private User createTestUser() {
        LocalDateTime now = LocalDateTime.now();
        return User.builder()
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .username("testuser")
                .password("password123")
                .createdAt(now)
                .updatedAt(now)
                .active(true)
                .build();
    }
}