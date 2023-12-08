package io.sultanov.taskmanagementsystem.mappers;

import io.sultanov.taskmanagementsystem.models.Task;
import io.sultanov.taskmanagementsystem.models.dto.TaskDto;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public Task dtoToTask(TaskDto taskDto) {
        Task task = new Task();
        task.setId(task.getId());
        task.setStatus(taskDto.getStatus());
        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setPriority(taskDto.getPriority());
        return task;
    }
}
