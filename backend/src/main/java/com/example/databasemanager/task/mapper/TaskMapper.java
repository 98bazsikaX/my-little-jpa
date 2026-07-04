package com.example.databasemanager.task.mapper;

import com.example.databasemanager.task.dto.TaskDto;
import com.example.databasemanager.task.entity.Task;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    TaskDto toDto(Task entity);

    Task toEntity(TaskDto dto);

    List<TaskDto> toDtoList(List<Task> entities);
}
