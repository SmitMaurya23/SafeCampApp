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

import com.example.safecamp.dto.EntryExitResponse;
import com.example.safecamp.dto.EntryRequest;
import com.example.safecamp.dto.ExitRequest;
import com.example.safecamp.entity.EntryExitLog;
import com.example.safecamp.entity.Gate;
import com.example.safecamp.entity.GuestVisit;
import com.example.safecamp.entity.User;
import com.example.safecamp.enums.GuestVisitStatus;
import com.example.safecamp.enums.MovementStatus;
import com.example.safecamp.enums.Role;
import com.example.safecamp.repository.EntryExitLogRepository;
import com.example.safecamp.repository.GateAssignmentRepository;
import com.example.safecamp.repository.GateRepository;
import com.example.safecamp.repository.GuestVisitRepository;
import com.example.safecamp.repository.UserRepository;
import com.example.safecamp.serviceImpl.EntryExitServiceImpl;

@ExtendWith(MockitoExtension.class)
class EntryExitServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GateRepository gateRepository;

    @Mock
    private EntryExitLogRepository entryExitLogRepository;

    @Mock
    private GuestVisitRepository guestVisitRepository;

    @Mock
    private GateAssignmentRepository gateAssignmentRepository;

    @InjectMocks
    private EntryExitServiceImpl entryExitService;

    private User guard;
    private User resident;
    private User guest;

    private Gate gate;

    private UUID guardId;
    private UUID gateId;

    @BeforeEach
    void setUp() {
        guardId = UUID.randomUUID();
        gateId = UUID.randomUUID();

        guard = new User();
        guard.setId(guardId);
        guard.setRole(Role.SECURITY);
        guard.setName("Guard");

        resident = new User();
        resident.setId(UUID.randomUUID());
        resident.setRole(Role.RESIDENT);
        resident.setName("Resident");

        guest = new User();
        guest.setId(UUID.randomUUID());
        guest.setRole(Role.GUEST);
        guest.setName("Guest");

        gate = new Gate();
        gate.setId(gateId);
        gate.setName("Main Gate");
    }

    // ================= MARK ENTRY =================

    @Test
    void markEntry_resident_success() {
        EntryRequest request = new EntryRequest(resident.getId(), gateId);

        when(userRepository.findById(resident.getId())).thenReturn(Optional.of(resident));
        when(userRepository.findById(guardId)).thenReturn(Optional.of(guard));
        when(gateRepository.findById(gateId)).thenReturn(Optional.of(gate));
        when(gateAssignmentRepository.isGuardAssignedToGate(any(), any(), any()))
                .thenReturn(true);
        when(entryExitLogRepository.findByUserAndStatus(resident, MovementStatus.IN))
                .thenReturn(Optional.empty());

        EntryExitResponse response = entryExitService.markEntry(request, guardId);

        assertEquals(MovementStatus.IN, response.getStatus());
        assertEquals("Main Gate", response.getEntryGateName());
        assertNull(response.getExitGateName());

        verify(entryExitLogRepository).save(any(EntryExitLog.class));
        verify(guestVisitRepository, never()).save(any());
    }

    @Test
    void markEntry_guest_success() {
        EntryRequest request = new EntryRequest(guest.getId(), gateId);

        GuestVisit visit = new GuestVisit();
        visit.setStatus(GuestVisitStatus.APPROVED);

        when(userRepository.findById(guest.getId())).thenReturn(Optional.of(guest));
        when(userRepository.findById(guardId)).thenReturn(Optional.of(guard));
        when(gateRepository.findById(gateId)).thenReturn(Optional.of(gate));
        when(gateAssignmentRepository.isGuardAssignedToGate(any(), any(), any()))
                .thenReturn(true);
        when(entryExitLogRepository.findByUserAndStatus(guest, MovementStatus.IN))
                .thenReturn(Optional.empty());
        when(guestVisitRepository.findByGuestAndStatus(guest, GuestVisitStatus.APPROVED))
                .thenReturn(Optional.of(visit));

        entryExitService.markEntry(request, guardId);

        assertEquals(GuestVisitStatus.ENTERED, visit.getStatus());
        verify(guestVisitRepository).save(visit);
    }

    @Test
    void markEntry_userAlreadyInside() {
        EntryRequest request = new EntryRequest(resident.getId(), gateId);

        when(userRepository.findById(resident.getId())).thenReturn(Optional.of(resident));
        when(userRepository.findById(guardId)).thenReturn(Optional.of(guard));
        when(gateRepository.findById(gateId)).thenReturn(Optional.of(gate));
        when(gateAssignmentRepository.isGuardAssignedToGate(any(), any(), any()))
                .thenReturn(true);
        when(entryExitLogRepository.findByUserAndStatus(resident, MovementStatus.IN))
                .thenReturn(Optional.of(new EntryExitLog()));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> entryExitService.markEntry(request, guardId));

        assertEquals("User is already inside campus", ex.getMessage());
    }

    // ================= MARK EXIT =================

    @Test
    void markExit_resident_success() {
        ExitRequest request = new ExitRequest(resident.getId(), gateId);

        EntryExitLog log = new EntryExitLog();
        log.setUser(resident);
        log.setEntryGate(gate);
        log.setEntryTime(LocalDateTime.now());
        log.setStatus(MovementStatus.IN);

        when(userRepository.findById(resident.getId())).thenReturn(Optional.of(resident));
        when(userRepository.findById(guardId)).thenReturn(Optional.of(guard));
        when(gateRepository.findById(gateId)).thenReturn(Optional.of(gate));
        when(gateAssignmentRepository.isGuardAssignedToGate(any(), any(), any()))
                .thenReturn(true);
        when(entryExitLogRepository.findByUserAndStatus(resident, MovementStatus.IN))
                .thenReturn(Optional.of(log));

        EntryExitResponse response = entryExitService.markExit(request, guardId);

        assertEquals(MovementStatus.OUT, response.getStatus());
        assertNotNull(response.getExitTime());

        verify(entryExitLogRepository).save(log);
    }

    @Test
    void markExit_guest_success() {
        ExitRequest request = new ExitRequest(guest.getId(), gateId);

        EntryExitLog log = new EntryExitLog();
        log.setUser(guest);
        log.setEntryGate(gate);
        log.setEntryTime(LocalDateTime.now());
        log.setStatus(MovementStatus.IN);

        GuestVisit visit = new GuestVisit();
        visit.setStatus(GuestVisitStatus.ENTERED);

        when(userRepository.findById(guest.getId())).thenReturn(Optional.of(guest));
        when(userRepository.findById(guardId)).thenReturn(Optional.of(guard));
        when(gateRepository.findById(gateId)).thenReturn(Optional.of(gate));
        when(gateAssignmentRepository.isGuardAssignedToGate(any(), any(), any()))
                .thenReturn(true);
        when(entryExitLogRepository.findByUserAndStatus(guest, MovementStatus.IN))
                .thenReturn(Optional.of(log));
        when(guestVisitRepository.findByGuestAndStatus(guest, GuestVisitStatus.ENTERED))
                .thenReturn(Optional.of(visit));

        entryExitService.markExit(request, guardId);

        assertEquals(GuestVisitStatus.EXITED, visit.getStatus());
        verify(guestVisitRepository).save(visit);
    }

    @Test
    void markExit_userNotInside() {
        ExitRequest request = new ExitRequest(resident.getId(), gateId);

        when(userRepository.findById(resident.getId())).thenReturn(Optional.of(resident));
        when(userRepository.findById(guardId)).thenReturn(Optional.of(guard));
        when(gateRepository.findById(gateId)).thenReturn(Optional.of(gate));
        when(gateAssignmentRepository.isGuardAssignedToGate(any(), any(), any()))
                .thenReturn(true);
        when(entryExitLogRepository.findByUserAndStatus(resident, MovementStatus.IN))
                .thenReturn(Optional.empty());

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> entryExitService.markExit(request, guardId));

        assertEquals("User is not inside campus", ex.getMessage());
    }
}
