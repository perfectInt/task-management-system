package io.sultanov.taskmanagementsystem.models.dto;

import io.sultanov.taskmanagementsystem.utils.Priority;
import io.sultanov.taskmanagementsystem.utils.Status;
import lombok.Data;

import java.util.List;

@Data
public class TaskDto {
    private Long id;
    private String title;
    private String description;
    private Status status;
    private Priority priority;
    private List<String> executors;
}
