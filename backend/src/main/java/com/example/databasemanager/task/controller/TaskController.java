package com.example.databasemanager.task.controller;

import com.example.databasemanager.task.dto.TaskDto;
import com.example.databasemanager.task.mapper.TaskMapper;
import com.example.databasemanager.task.repository.TaskRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** REST controller for the {@code /api/tasks} endpoint. */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public TaskController(TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    /**
     * Returns all tasks (no pagination).
     *
     * @return list of all task DTOs
     */
    @GetMapping
    public List<TaskDto> getAllTasks() {
        return taskMapper.toDtoList(taskRepository.findAll());
    }
}
