package com.example.databasemanager.user.service;

import com.example.databasemanager.common.exception.DuplicateResourceException;
import com.example.databasemanager.user.dto.CreateUserRequest;
import com.example.databasemanager.user.dto.UserDto;
import com.example.databasemanager.user.dto.UserFilter;
import com.example.databasemanager.user.entity.User;
import com.example.databasemanager.user.mapper.UserMapper;
import com.example.databasemanager.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> getAllUsers(Pageable pageable) {
        Page<User> page = userRepository.findAll(pageable);
        return new PageImpl<>(
                userMapper.toDtoList(page.getContent()),
                pageable,
                page.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> queryUsers(UserFilter filter, Pageable pageable) {
        Specification<User> spec = filter.toSpecification();
        Page<User> page = userRepository.findAll(spec, pageable);
        return page.map(userMapper::toDto);
    }

    @Override
    public UserDto createUser(CreateUserRequest request) {
        validateUniqueness(request.getUserName(), request.getEmail(), null);
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found: " + id);
        }
        userRepository.deleteById(id);
    }

    private void validateUniqueness(String userName, String email, Long excludeId) {
        userRepository.findByUserName(userName).ifPresent(existing -> {
            if (!existing.getId().equals(excludeId)) {
                throw new DuplicateResourceException("Username already taken: " + userName);
            }
        });
        userRepository.findByEmail(email).ifPresent(existing -> {
            if (!existing.getId().equals(excludeId)) {
                throw new DuplicateResourceException("Email already taken: " + email);
            }
        });
    }
}
