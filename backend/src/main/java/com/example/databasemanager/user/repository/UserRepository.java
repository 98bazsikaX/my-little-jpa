package com.example.databasemanager.user.repository;

import com.example.databasemanager.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

/**
 * JPA repository for {@link User}. Supports both standard CRUD and
 * {@link org.springframework.data.jpa.domain.Specification}-based filtering
 * via {@link JpaSpecificationExecutor}.
 */
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByUserName(String userName);

    Optional<User> findByEmail(String email);

    boolean existsByUserName(String userName);

    boolean existsByEmail(String email);
}
