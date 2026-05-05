package com.example.chronos.repository;

import com.example.chronos.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String name);

    Optional<User> findByUsername(String username);
}
