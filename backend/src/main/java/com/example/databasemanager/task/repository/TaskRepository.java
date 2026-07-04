package com.example.databasemanager.task.repository;

import com.example.databasemanager.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
