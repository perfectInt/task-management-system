package io.sultanov.taskmanagementsystem.models.dto;

import io.sultanov.taskmanagementsystem.utils.Status;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeStatusDto {
    @NotNull(message = "Id cannot be blank")
    private Long id;
    @NotNull(message = "You must set a status")
    private Status status;
}
