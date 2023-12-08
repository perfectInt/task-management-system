package io.sultanov.taskmanagementsystem.controllers;

import io.sultanov.taskmanagementsystem.models.Task;
import io.sultanov.taskmanagementsystem.models.dto.ChangeStatusDto;
import io.sultanov.taskmanagementsystem.models.dto.CommentDto;
import io.sultanov.taskmanagementsystem.models.dto.TaskDto;
import io.sultanov.taskmanagementsystem.services.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public Task createTask(@Valid @RequestBody TaskDto taskDto) {
        return taskService.createTask(taskDto);
    }

    @GetMapping
    public List<Task> checkAllUsersTasks(@RequestParam(name = "page", required = false) Integer page) {
        return taskService.getAllUsersTasks(page);
    }

    @PutMapping
    public void updateTask(@Valid @RequestBody TaskDto taskDto) {
        taskService.editTask(taskDto);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }

    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    @PutMapping("/status")
    public ResponseEntity<?> changeStatus(@Valid @RequestBody ChangeStatusDto changeStatusDto) {
        return taskService.changeStatus(changeStatusDto);
    }

    @PutMapping("/comments")
    public Task addComment(@Valid @RequestBody CommentDto commentDto, @RequestParam(name = "task_id") Long taskId) {
        return taskService.addComment(taskId, commentDto);
    }

    @GetMapping("/authors")
    public List<Task> getAuthorTasks(@RequestParam(name = "author") String author,
                                     @RequestParam(name = "page", required = false) Integer page) {
        return taskService.getTasksByAuthorName(author, page);
    }

    @GetMapping("/executors")
    public List<Task> getExecutorTasks(@RequestParam(name = "executor") String executor,
                                       @RequestParam(name = "page", required = false) Integer page) {
        return taskService.getTasksByExecutorName(executor, page);
    }
}
