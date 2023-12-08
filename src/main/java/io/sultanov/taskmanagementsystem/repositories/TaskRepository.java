package io.sultanov.taskmanagementsystem.repositories;

import io.sultanov.taskmanagementsystem.models.Task;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByAuthor(String author, Pageable pageable);

    @Query("SELECT t FROM Task t JOIN t.executors u WHERE u.email = :executor")
    List<Task> getTasksByExecutorsName(String executor, Pageable pageable);
}
