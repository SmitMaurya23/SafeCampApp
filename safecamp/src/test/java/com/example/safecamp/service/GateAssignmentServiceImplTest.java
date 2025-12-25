package com.example.safecamp.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.safecamp.dto.GateAssignmentResponse;
import com.example.safecamp.entity.Gate;
import com.example.safecamp.entity.GateAssignment;
import com.example.safecamp.entity.User;
import com.example.safecamp.enums.Role;
import com.example.safecamp.repository.GateAssignmentRepository;
import com.example.safecamp.repository.GateRepository;
import com.example.safecamp.repository.UserRepository;
import com.example.safecamp.serviceImpl.GateAssignmentServiceImpl;

@ExtendWith(MockitoExtension.class)
class GateAssignmentServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GateRepository gateRepository;

    @Mock
    private GateAssignmentRepository gateAssignmentRepository;

    @InjectMocks
    private GateAssignmentServiceImpl gateAssignmentService;

    private UUID guardId;
    private UUID adminId;
    private UUID gateId;
    private UUID assignmentId;

    private User guard;
    private User admin;
    private Gate gate;
    private GateAssignment assignment;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @BeforeEach
    void setUp() {
        guardId = UUID.randomUUID();
        adminId = UUID.randomUUID();
        gateId = UUID.randomUUID();
        assignmentId = UUID.randomUUID();

        startTime = LocalDateTime.now().minusHours(1);
        endTime = LocalDateTime.now().plusHours(4);

        guard = new User();
        guard.setId(guardId);
        guard.setName("Security Guard");
        guard.setRole(Role.SECURITY);

        admin = new User();
        admin.setId(adminId);
        admin.setName("Admin User");
        admin.setRole(Role.ADMIN);

        gate = new Gate();
        gate.setId(gateId);
        gate.setName("Main Gate");

        assignment = GateAssignment.builder()
                .id(assignmentId)
                .guard(guard)
                .gate(gate)
                .assignedBy(admin)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }

    // ---------- assignGuardToGate ----------

    @Test
    void assignGuardToGate_success() {
        when(userRepository.findById(guardId)).thenReturn(Optional.of(guard));
        when(userRepository.findById(adminId)).thenReturn(Optional.of(admin));
        when(gateRepository.findById(gateId)).thenReturn(Optional.of(gate));
        when(gateAssignmentRepository.save(any(GateAssignment.class))).thenReturn(assignment);

        GateAssignmentResponse response = gateAssignmentService.assignGuardToGate(
                guardId, gateId, startTime, endTime, adminId);

        assertNotNull(response);
        assertEquals(guardId, response.getGuardId());
        assertEquals(guard.getName(), response.getGuardName());
        assertEquals(gateId, response.getGateId());
        assertEquals(gate.getName(), response.getGateName());
        assertEquals(startTime, response.getStartTime());
        assertEquals(endTime, response.getEndTime());

        verify(gateAssignmentRepository).save(any(GateAssignment.class));
    }

    @Test
    void assignGuardToGate_guardNotFound() {
        when(userRepository.findById(guardId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> gateAssignmentService.assignGuardToGate(
                        guardId, gateId, startTime, endTime, adminId)
        );

        assertEquals("Guard not found", ex.getMessage());
    }

    @Test
    void assignGuardToGate_userNotSecurity() {
        guard.setRole(Role.RESIDENT);

        when(userRepository.findById(guardId)).thenReturn(Optional.of(guard));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> gateAssignmentService.assignGuardToGate(
                        guardId, gateId, startTime, endTime, adminId)
        );

        assertEquals("Assigned user is not a security guard", ex.getMessage());
    }

    @Test
    void assignGuardToGate_adminNotFound() {
        when(userRepository.findById(guardId)).thenReturn(Optional.of(guard));
        when(userRepository.findById(adminId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> gateAssignmentService.assignGuardToGate(
                        guardId, gateId, startTime, endTime, adminId)
        );

        assertEquals("Admin not found", ex.getMessage());
    }

    @Test
    void assignGuardToGate_gateNotFound() {
        when(userRepository.findById(guardId)).thenReturn(Optional.of(guard));
        when(userRepository.findById(adminId)).thenReturn(Optional.of(admin));
        when(gateRepository.findById(gateId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> gateAssignmentService.assignGuardToGate(
                        guardId, gateId, startTime, endTime, adminId)
        );

        assertEquals("Gate not found", ex.getMessage());
    }

    @Test
    void assignGuardToGate_endTimeBeforeStartTime() {
        LocalDateTime invalidEndTime = startTime.minusHours(2);

        when(userRepository.findById(guardId)).thenReturn(Optional.of(guard));
        when(userRepository.findById(adminId)).thenReturn(Optional.of(admin));
        when(gateRepository.findById(gateId)).thenReturn(Optional.of(gate));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> gateAssignmentService.assignGuardToGate(
                        guardId, gateId, startTime, invalidEndTime, adminId)
        );

        assertEquals("End time cannot be before start time", ex.getMessage());
    }

    // ---------- endAssignment ----------

    @Test
    void endAssignment_success() {
        when(gateAssignmentRepository.findById(assignmentId))
                .thenReturn(Optional.of(assignment));

        gateAssignmentService.endAssignment(assignmentId, adminId);

        assertNotNull(assignment.getEndTime());
        verify(gateAssignmentRepository).save(assignment);
    }

    @Test
    void endAssignment_assignmentNotFound() {
        when(gateAssignmentRepository.findById(assignmentId))
                .thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> gateAssignmentService.endAssignment(assignmentId, adminId)
        );

        assertEquals("Assignment not found", ex.getMessage());
    }
}
