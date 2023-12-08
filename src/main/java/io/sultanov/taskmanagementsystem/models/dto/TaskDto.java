package io.sultanov.taskmanagementsystem.models.dto;

import io.sultanov.taskmanagementsystem.utils.Priority;
import io.sultanov.taskmanagementsystem.utils.Status;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class TaskDto {
    private Long id;
    @NotBlank(message = "The title of task mustn't be blank!")
    private String title;
    private String description;
    @NotBlank(message = "The status of task mustn't be blank!")
    private Status status;
    @NotBlank(message = "The priority of task mustn't be blank!")
    private Priority priority;
    private List<String> executors;
}
