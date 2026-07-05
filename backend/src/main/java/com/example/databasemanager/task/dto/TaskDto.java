package com.example.databasemanager.task.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/** DTO for {@link Task} entity returned by the API. */
@Getter
@Setter
@Builder
public class TaskDto {

    private Long id;
    private String title;
    private boolean completed;
}
