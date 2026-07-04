package com.example.databasemanager.task.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskDto {

    private Long id;
    private String title;
    private boolean completed;
}
