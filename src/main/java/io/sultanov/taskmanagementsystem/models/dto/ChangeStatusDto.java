package io.sultanov.taskmanagementsystem.models.dto;

import io.sultanov.taskmanagementsystem.utils.Status;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangeStatusDto {
    @NotBlank(message = "Id cannot be blank")
    private Long id;
    @NotBlank(message = "You must set a status")
    private Status status;
}
