package com.example.safecamp.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.safecamp.entity.EmergencyAlert;
import com.example.safecamp.entity.User;
import com.example.safecamp.enums.EmergencyStatus;

@Repository
public interface EmergencyAlertRepository extends JpaRepository<EmergencyAlert, UUID> {
    boolean existsByUserAndStatus(User user, EmergencyStatus status);

}
