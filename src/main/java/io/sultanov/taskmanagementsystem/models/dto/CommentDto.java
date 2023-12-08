package io.sultanov.taskmanagementsystem.models.dto;

import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentDto {
    @NotBlank(message = "You must write something to leave a comment!", payload = Payload.class)
    private String comment;
}
