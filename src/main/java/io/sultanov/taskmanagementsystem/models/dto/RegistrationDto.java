package io.sultanov.taskmanagementsystem.models.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistrationDto {
    @Pattern(regexp = "[a-zA-Z0-9-.]+@[a-zA-Z0-9]+\\.[a-zA-Z0-9]+")
    private String email;
    @Size(message = "Password must be greater than 8!", min = 8)
    private String password;
    @Size(message = "Password confirmation must be greater than 8!", min = 8)
    private String passwordConfirmation;
}
