package io.sultanov.taskmanagementsystem.exceptions.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorMessage {
    private String body;
}
