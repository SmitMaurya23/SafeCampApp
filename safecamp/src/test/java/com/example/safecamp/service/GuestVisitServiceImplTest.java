package com.example.safecamp.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.safecamp.dto.CreateGuestVisitRequest;
import com.example.safecamp.dto.GuestVisitResponse;
import com.example.safecamp.entity.Gate;
import com.example.safecamp.entity.GuestVisit;
import com.example.safecamp.entity.User;
import com.example.safecamp.enums.GuestVisitStatus;
import com.example.safecamp.enums.Role;
import com.example.safecamp.repository.GateRepository;
import com.example.safecamp.repository.GuestVisitRepository;
import com.example.safecamp.repository.UserRepository;
import com.example.safecamp.serviceImpl.GuestVisitServiceImpl;

@ExtendWith(MockitoExtension.class)
class GuestVisitServiceImplTest {

    @Mock
    private GuestVisitRepository guestVisitRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GateRepository gateRepository;

    @InjectMocks
    private GuestVisitServiceImpl guestVisitService;

    private UUID hostId;
    private UUID guestId;
    private UUID gateId;

    private User host;
    private User guest;
    private Gate gate;
    private GuestVisit savedVisit;
    private CreateGuestVisitRequest request;

    @BeforeEach
    void setUp() {
        hostId = UUID.randomUUID();
        guestId = UUID.randomUUID();
        gateId = UUID.randomUUID();

        host = new User();
        host.setId(hostId);
        host.setName("Host Resident");
        host.setRole(Role.RESIDENT);

        guest = new User();
        guest.setId(guestId);
        guest.setName("Guest User");
        guest.setPhone("8888888888");
        guest.setRole(Role.GUEST);

        gate = new Gate();
        gate.setId(gateId);
        gate.setName("Main Gate");

        request = new CreateGuestVisitRequest();
        request.setGuestId(guestId);
        request.setExpectedGateId(gateId);
        request.setExpectedEntryTime(LocalDateTime.now().plusHours(1));

        savedVisit = new GuestVisit();
        savedVisit.setId(UUID.randomUUID());
        savedVisit.setGuest(guest);
        savedVisit.setHostResident(host);
        savedVisit.setExpectedGate(gate);
        savedVisit.setExpectedEntryTime(request.getExpectedEntryTime());
        savedVisit.setStatus(GuestVisitStatus.PENDING);
    }

    // ---------- createGuestVisit ----------

    @Test
    void createGuestVisit_success() {
        when(userRepository.findById(hostId)).thenReturn(Optional.of(host));
        when(userRepository.findById(guestId)).thenReturn(Optional.of(guest));
        when(guestVisitRepository.existsByGuestAndStatusIn(
                eq(guest),
                eq(List.of(
                        GuestVisitStatus.PENDING,
                        GuestVisitStatus.APPROVED,
                        GuestVisitStatus.ENTERED))
        )).thenReturn(false);
        when(gateRepository.findById(gateId)).thenReturn(Optional.of(gate));
        when(guestVisitRepository.save(any(GuestVisit.class))).thenReturn(savedVisit);

        GuestVisitResponse response = guestVisitService.createGuestVisit(request, hostId);

        assertNotNull(response);
        assertEquals(savedVisit.getId(), response.getVisitId());
        assertEquals(guest.getId(), response.getGuestId());
        assertEquals(guest.getName(), response.getGuestName());
        assertEquals(guest.getPhone(), response.getGuestPhone());
        assertEquals(host.getId(), response.getHostResidentId());
        assertEquals(host.getName(), response.getHostResidentName());
        assertEquals(gate.getId(), response.getExpectedGateId());
        assertEquals(gate.getName(), response.getExpectedGateName());
        assertEquals(savedVisit.getExpectedEntryTime(), response.getExpectedEntryTime());
        assertEquals(GuestVisitStatus.PENDING, response.getStatus());

        verify(guestVisitRepository).save(any(GuestVisit.class));
    }

    @Test
    void createGuestVisit_hostNotFound_throwsException() {
        when(userRepository.findById(hostId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> guestVisitService.createGuestVisit(request, hostId)
        );

        assertEquals("Host resident not found", ex.getMessage());
    }

    @Test
    void createGuestVisit_guestNotFound_throwsException() {
        when(userRepository.findById(hostId)).thenReturn(Optional.of(host));
        when(userRepository.findById(guestId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> guestVisitService.createGuestVisit(request, hostId)
        );

        assertEquals("Guest not found", ex.getMessage());
    }

    @Test
    void createGuestVisit_userIsNotGuest_throwsException() {
        guest.setRole(Role.RESIDENT);

        when(userRepository.findById(hostId)).thenReturn(Optional.of(host));
        when(userRepository.findById(guestId)).thenReturn(Optional.of(guest));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> guestVisitService.createGuestVisit(request, hostId)
        );

        assertEquals("Provided user is not a guest", ex.getMessage());
    }

    @Test
    void createGuestVisit_guestAlreadyHasActiveVisit_throwsException() {
        when(userRepository.findById(hostId)).thenReturn(Optional.of(host));
        when(userRepository.findById(guestId)).thenReturn(Optional.of(guest));
        when(guestVisitRepository.existsByGuestAndStatusIn(
                any(),
                any()
        )).thenReturn(true);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> guestVisitService.createGuestVisit(request, hostId)
        );

        assertEquals("Guest already has an active visit", ex.getMessage());
    }

    @Test
    void createGuestVisit_gateNotFound_throwsException() {
        when(userRepository.findById(hostId)).thenReturn(Optional.of(host));
        when(userRepository.findById(guestId)).thenReturn(Optional.of(guest));
        when(guestVisitRepository.existsByGuestAndStatusIn(any(), any())).thenReturn(false);
        when(gateRepository.findById(gateId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> guestVisitService.createGuestVisit(request, hostId)
        );

        assertEquals("Gate not found", ex.getMessage());
    }
}
