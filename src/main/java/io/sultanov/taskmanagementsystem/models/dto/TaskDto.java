package io.sultanov.taskmanagementsystem.models.dto;

import io.sultanov.taskmanagementsystem.utils.Priority;
import io.sultanov.taskmanagementsystem.utils.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class TaskDto {
    private Long id;
    @NotBlank(message = "The title of task mustn't be blank!")
    private String title;
    private String description;
    @NotNull(message = "Must not be empty")
    private Status status;
    @NotNull(message = "Must not be empty")
    private Priority priority;
    private List<String> executors;
}
