package com.example.safecamp.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

import com.example.safecamp.dto.GuestVisitResponse;
import com.example.safecamp.entity.Gate;
import com.example.safecamp.entity.GuestVisit;
import com.example.safecamp.entity.User;
import com.example.safecamp.enums.GuestVisitStatus;
import com.example.safecamp.enums.Role;
import com.example.safecamp.repository.GateAssignmentRepository;
import com.example.safecamp.repository.GuestVisitRepository;
import com.example.safecamp.repository.UserRepository;
import com.example.safecamp.serviceImpl.GuestVisitApprovalServiceImpl;

@ExtendWith(MockitoExtension.class)
class GuestVisitApprovalServiceImplTest {

    @Mock
    private GuestVisitRepository guestVisitRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GateAssignmentRepository gateAssignmentRepository;

    @InjectMocks
    private GuestVisitApprovalServiceImpl approvalService;

    private UUID visitId;
    private UUID approverId;

    private User approver;
    private User guest;
    private User host;
    private Gate gate;
    private GuestVisit visit;

    @BeforeEach
    void setUp() {
        visitId = UUID.randomUUID();
        approverId = UUID.randomUUID();

        approver = new User();
        approver.setId(approverId);
        approver.setName("Security Guard");
        approver.setRole(Role.SECURITY);

        guest = new User();
        guest.setId(UUID.randomUUID());
        guest.setName("Guest");
        guest.setPhone("9999999999");
        guest.setRole(Role.GUEST);

        host = new User();
        host.setId(UUID.randomUUID());
        host.setName("Resident");
        host.setRole(Role.RESIDENT);

        gate = new Gate();
        gate.setId(UUID.randomUUID());
        gate.setName("Main Gate");

        visit = new GuestVisit();
        visit.setId(visitId);
        visit.setGuest(guest);
        visit.setHostResident(host);
        visit.setExpectedGate(gate);
        visit.setExpectedEntryTime(LocalDateTime.now().plusHours(1));
        visit.setStatus(GuestVisitStatus.PENDING);
    }

    // ================= APPROVE =================

    @Test
    void approveVisit_success() {
        when(userRepository.findById(approverId)).thenReturn(Optional.of(approver));
        when(guestVisitRepository.findById(visitId)).thenReturn(Optional.of(visit));
        when(gateAssignmentRepository.isGuardAssignedToGate(
                eq(approver), eq(gate), any(LocalDateTime.class)))
                .thenReturn(true);
        when(guestVisitRepository.save(any(GuestVisit.class))).thenReturn(visit);

        GuestVisitResponse response = approvalService.approveVisit(visitId, approverId);

        assertNotNull(response);
        assertEquals(GuestVisitStatus.APPROVED, response.getStatus());
        assertEquals(visitId, response.getVisitId());
        assertEquals(guest.getId(), response.getGuestId());
        assertEquals(host.getId(), response.getHostResidentId());

        verify(guestVisitRepository).save(visit);
    }

    @Test
    void approveVisit_approverNotFound() {
        when(userRepository.findById(approverId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> approvalService.approveVisit(visitId, approverId));

        assertEquals("Approver not found", ex.getMessage());
    }

    @Test
    void approveVisit_visitNotFound() {
        when(userRepository.findById(approverId)).thenReturn(Optional.of(approver));
        when(guestVisitRepository.findById(visitId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> approvalService.approveVisit(visitId, approverId));

        assertEquals("Guest visit not found", ex.getMessage());
    }

    @Test
    void approveVisit_guardNotAssigned() {
        when(userRepository.findById(approverId)).thenReturn(Optional.of(approver));
        when(guestVisitRepository.findById(visitId)).thenReturn(Optional.of(visit));
        when(gateAssignmentRepository.isGuardAssignedToGate(any(), any(), any()))
                .thenReturn(false);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> approvalService.approveVisit(visitId, approverId));

        assertEquals("Guard not assigned to visit gate", ex.getMessage());
    }

    @Test
    void approveVisit_notPending() {
        visit.setStatus(GuestVisitStatus.APPROVED);

        when(userRepository.findById(approverId)).thenReturn(Optional.of(approver));
        when(guestVisitRepository.findById(visitId)).thenReturn(Optional.of(visit));
        when(gateAssignmentRepository.isGuardAssignedToGate(any(), any(), any()))
                .thenReturn(true);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> approvalService.approveVisit(visitId, approverId));

        assertEquals("Only pending visits can be approved", ex.getMessage());
    }

    // ================= REJECT =================

    @Test
    void rejectVisit_success() {
        when(userRepository.findById(approverId)).thenReturn(Optional.of(approver));
        when(guestVisitRepository.findById(visitId)).thenReturn(Optional.of(visit));
        when(gateAssignmentRepository.isGuardAssignedToGate(any(), any(), any()))
                .thenReturn(true);
        when(guestVisitRepository.save(any(GuestVisit.class))).thenReturn(visit);

        GuestVisitResponse response = approvalService.rejectVisit(visitId, approverId);

        assertNotNull(response);
        assertEquals(GuestVisitStatus.REJECTED, response.getStatus());

        verify(guestVisitRepository).save(visit);
    }

    @Test
    void rejectVisit_notPending() {
        visit.setStatus(GuestVisitStatus.APPROVED);

        when(userRepository.findById(approverId)).thenReturn(Optional.of(approver));
        when(guestVisitRepository.findById(visitId)).thenReturn(Optional.of(visit));
        when(gateAssignmentRepository.isGuardAssignedToGate(any(), any(), any()))
                .thenReturn(true);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> approvalService.rejectVisit(visitId, approverId));

        assertEquals("Only pending visits can be rejected", ex.getMessage());
    }
}
