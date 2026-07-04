package com.example.databasemanager.integration.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.databasemanager.user.entity.User;
import com.example.databasemanager.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User savedUser;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUserName("testuser");
        user.setEmail("test@example.com");
        user.setPassword("encoded");
        user.setFirstName("Test");
        user.setLastName("User");
        savedUser = userRepository.save(user);
    }

    @Test
    void shouldFindByUserName() {
        Optional<User> found = userRepository.findByUserName("testuser");
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void shouldReturnEmptyForUnknownUserName() {
        Optional<User> found = userRepository.findByUserName("nobody");
        assertThat(found).isEmpty();
    }

    @Test
    void shouldFindByEmail() {
        Optional<User> found = userRepository.findByEmail("test@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().getUserName()).isEqualTo("testuser");
    }

    @Test
    void shouldCheckExistsByUserName() {
        assertThat(userRepository.existsByUserName("testuser")).isTrue();
        assertThat(userRepository.existsByUserName("nobody")).isFalse();
    }

    @Test
    void shouldCheckExistsByEmail() {
        assertThat(userRepository.existsByEmail("test@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("nobody@example.com")).isFalse();
    }

    @Test
    void shouldGenerateIdOnSave() {
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getId()).isPositive();
    }

    @Test
    void shouldSetCreatedTimestamp() {
        assertThat(savedUser.getCreated()).isNotNull();
    }

    @Test
    void shouldSetUpdatedTimestampOnUpdate() {
        User found = userRepository.findById(savedUser.getId()).orElseThrow();
        found.setLastName("Updated");
        User updated = userRepository.save(found);
        userRepository.flush();
        assertThat(updated.getUpdated()).isNotNull();
    }
}
