package io.sultanov.taskmanagementsystem.models;

import io.sultanov.taskmanagementsystem.utils.Priority;
import io.sultanov.taskmanagementsystem.utils.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "t_tasks")
@Getter
@Setter
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    private String description;
    private Status status;
    private Priority priority;
    private String author;
    private String executor;
}
