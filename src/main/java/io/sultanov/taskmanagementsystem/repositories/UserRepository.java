package io.sultanov.taskmanagementsystem.repositories;

import io.sultanov.taskmanagementsystem.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query(nativeQuery = true,
        value = "SELECT * FROM t_users u WHERE u.email IN ?1")
    List<User> findAllByEmailInArgs(List<String> emails);
}
