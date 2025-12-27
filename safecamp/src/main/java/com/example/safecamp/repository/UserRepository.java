package com.example.safecamp.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.safecamp.entity.User;
import com.example.safecamp.enums.Role;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);

    Optional<User> findByPhone(String phone);

    Optional<User> findByEmail(String email);

    Optional<List<User>> findByName(String name);

    Optional<List<User>> findByRole(Role role);
}