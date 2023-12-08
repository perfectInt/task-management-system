package io.sultanov.taskmanagementsystem.models.dto;

import io.sultanov.taskmanagementsystem.utils.Status;
import lombok.Data;

@Data
public class ChangeStatusDto {
    private Long id;
    private Status status;
}
