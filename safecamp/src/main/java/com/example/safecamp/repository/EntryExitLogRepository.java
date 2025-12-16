package com.example.safecamp.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.safecamp.entity.EntryExitLog;
import com.example.safecamp.entity.User;
import com.example.safecamp.enums.MovementStatus;


@Repository
public interface EntryExitLogRepository
        extends JpaRepository<EntryExitLog, UUID> {

    Optional<EntryExitLog> findByUserAndStatus(
            User user,
            MovementStatus status
    );
}

