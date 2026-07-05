package com.example.databasemanager.user.mapper;

import com.example.databasemanager.user.dto.CreateUserRequest;
import com.example.databasemanager.user.dto.UserDto;
import com.example.databasemanager.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User entity);

    List<UserDto> toDtoList(List<User> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "updated", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    @Mapping(target = "password", ignore = true)
    User toEntity(CreateUserRequest request);
}
