package io.sultanov.taskmanagementsystem.services;

import io.sultanov.taskmanagementsystem.exceptions.ObjectNotFoundException;
import io.sultanov.taskmanagementsystem.mappers.TaskMapper;
import io.sultanov.taskmanagementsystem.models.Comment;
import io.sultanov.taskmanagementsystem.models.Task;
import io.sultanov.taskmanagementsystem.models.User;
import io.sultanov.taskmanagementsystem.models.dto.ChangeStatusDto;
import io.sultanov.taskmanagementsystem.models.dto.CommentDto;
import io.sultanov.taskmanagementsystem.models.dto.TaskDto;
import io.sultanov.taskmanagementsystem.repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;
    private final TaskMapper taskMapper;
    @Value("${pagination.size:10}")
    private Integer pageSize;

    public Task createTask(TaskDto taskDto) {
        Task task = taskMapper.mapToTask(taskDto);
        task.setExecutors(userService.getExecutors(taskDto.getExecutors()));
        task.setAuthor(SecurityContextHolder.getContext().getAuthentication().getName());
        return taskRepository.save(task);
    }

    public void editTask(TaskDto taskDto) {
        Task task = getTaskById(taskDto.getId());
        String author = SecurityContextHolder.getContext().getAuthentication().getName();
        if (task.getAuthor().equals(author)) {
            task = taskMapper.mapToTask(taskDto);
            task.setAuthor(author);
            taskRepository.save(task);
        }
    }

    public Task addComment(Long id, CommentDto commentDto) {
        Task task = getTaskById(id);
        Comment comment = new Comment();
        comment.setComment(commentDto.getComment());
        comment.setUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        comment.setTask(task);
        task.getComments().add(comment);
        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(ObjectNotFoundException::new);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (task.getAuthor().equals(username))
            taskRepository.deleteById(id);
    }

    public List<Task> getAllUsersTasks(Integer pageNum) {
        if (pageNum == null) pageNum = 0;
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        return taskRepository.findAllByAuthor(SecurityContextHolder.getContext().getAuthentication().getName(),
                pageable);
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow(ObjectNotFoundException::new);
    }

    public List<Task> getTasksByAuthorName(String author, Integer pageNum) {
        pageNum = pageNum != null ? pageNum : 0;
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        return taskRepository.findAllByAuthor(author, pageable);
    }

    public List<Task> getTasksByExecutorName(String executor, Integer pageNum) {
        if (pageNum == null) pageNum = 0;
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        return taskRepository.getTasksByExecutorsName(executor, pageable);
    }

    public ResponseEntity<?> changeStatus(ChangeStatusDto changeStatusDto) {
        Task task = getTaskById(changeStatusDto.getId());
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (task.getExecutors().stream().map(User::getEmail).toList().contains(username)) {
            task.setStatus(changeStatusDto.getStatus());
            return ResponseEntity.ok(taskRepository.save(task));
        } else {
            return new ResponseEntity<>("Cannot change status because you are not executor", HttpStatus.FORBIDDEN);
        }
    }
}
