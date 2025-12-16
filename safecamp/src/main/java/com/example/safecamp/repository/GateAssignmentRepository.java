package com.example.safecamp.repository;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.safecamp.entity.Gate;
import com.example.safecamp.entity.GateAssignment;
import com.example.safecamp.entity.User;

public interface GateAssignmentRepository extends JpaRepository<GateAssignment, UUID> {

        @Query("""
        SELECT COUNT(ga) > 0
        FROM GateAssignment ga
        WHERE ga.guard = :guard
          AND ga.gate = :gate
          AND ga.startTime <= :currentTime
          AND (ga.endTime IS NULL OR ga.endTime >= :currentTime)
    """)
    boolean isGuardAssignedToGate(
            User guard,
            Gate gate,
            LocalDateTime currentTime
    );
}
