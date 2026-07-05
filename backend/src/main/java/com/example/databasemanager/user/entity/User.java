package com.example.databasemanager.user.entity;

import com.example.databasemanager.common.AbstractModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/** JPA entity for the {@code "users"} table. Extends {@link com.example.databasemanager.common.AbstractModel} for id, created, updated. */
@Entity
@Table(name = "\"users\"")
@Getter
@Setter
@NoArgsConstructor
public class User extends AbstractModel {

    @Column(name = "user_name", length = 128, nullable = false, unique = true)
    private String userName;

    @Column(length = 128, nullable = false, unique = true)
    private String email;

    @Column(name = "first_name", length = 128)
    private String firstName;

    @Column(name = "last_name", length = 128)
    private String lastName;

    @Column(nullable = false)
    private String password;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;
}
