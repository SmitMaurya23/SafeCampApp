package com.example.safecamp.serviceImpl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.safecamp.dto.GateAssignmentResponse;
import com.example.safecamp.entity.Gate;
import com.example.safecamp.entity.GateAssignment;
import com.example.safecamp.entity.User;
import com.example.safecamp.enums.Role;
import com.example.safecamp.repository.GateAssignmentRepository;
import com.example.safecamp.repository.GateRepository;
import com.example.safecamp.repository.UserRepository;
import com.example.safecamp.service.GateAssignmentService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class GateAssignmentServiceImpl implements GateAssignmentService {

    private final UserRepository userRepository;
    private final GateRepository gateRepository;
    private final GateAssignmentRepository gateAssignmentRepository;

    @Override
    public GateAssignmentResponse assignGuardToGate(UUID guardId, UUID gateId, LocalDateTime startTime,
            LocalDateTime endTime, UUID adminId) {
        User guard = userRepository.findById(guardId)
                .orElseThrow(() -> new IllegalArgumentException("Guard not found"));

        if (guard.getRole() != Role.SECURITY) {
            throw new IllegalStateException("Assigned user is not a security guard");
        }


        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));


        Gate gate = gateRepository.findById(gateId)
                .orElseThrow(() -> new IllegalArgumentException("Gate not found"));

        if (endTime != null && endTime.isBefore(startTime)) {
            throw new IllegalStateException("End time cannot be before start time");
        }

        GateAssignment assignment = GateAssignment.builder()
                .guard(guard)
                .gate(gate)
                .assignedBy(admin)
                .startTime(startTime)
                .endTime(endTime)
                .build();

        GateAssignment saved = gateAssignmentRepository.save(assignment);

        return GateAssignmentResponse.builder()
                                        .assignmentId(saved.getId())
                                        .endTime(saved.getEndTime())
                                        .gateId(gate.getId())
                                        .gateName(gate.getName())
                                        .guardId(guard.getId())
                                        .guardName(guard.getName())
                                        .startTime(saved.getStartTime())
                                        .build();
    }

    @Override
    public void endAssignment(UUID assignmentId, UUID adminId) {

        GateAssignment assignment = gateAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));

        assignment.setEndTime(LocalDateTime.now());

        gateAssignmentRepository.save(assignment);
    }

}
