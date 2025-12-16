package com.example.safecamp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

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
import com.example.safecamp.service.serviceImpl.EntryExitServiceImpl;

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

        private User user;
        private User guest;
        private User guard;
        private Gate entryGate;
        private Gate exitGate;

        private EntryRequest entryRequest;
        private ExitRequest exitRequest;
        private UUID guardId;

        @BeforeEach
        void setup() {

                guardId = UUID.randomUUID();

                guard = new User();
                guard.setId(guardId);
                guard.setRole(Role.SECURITY);
                guard.setName("Guard A");

                user = new User();
                user.setId(UUID.randomUUID());
                user.setName("Amit");
                user.setRole(Role.RESIDENT);

                guest = new User();
                guest.setId(UUID.randomUUID());
                guest.setName("Rahul");
                guest.setRole(Role.GUEST);

                entryGate = new Gate();
                entryGate.setId(UUID.randomUUID());
                entryGate.setName("Main Gate");

                exitGate = new Gate();
                exitGate.setId(UUID.randomUUID());
                exitGate.setName("Back Gate");

                entryRequest = new EntryRequest(user.getId(), entryGate.getId());
                exitRequest = new ExitRequest(user.getId(), exitGate.getId());
        }

        // ---------------- ENTRY TESTS ----------------

        @Test
        void shouldAllowEntry_whenUserIsOutside() {

                when(userRepository.findById(user.getId()))
                                .thenReturn(Optional.of(user));

                when(userRepository.findById(guardId))
                                .thenReturn(Optional.of(guard));

                when(gateRepository.findById(entryGate.getId()))
                                .thenReturn(Optional.of(entryGate));

                when(gateAssignmentRepository.isGuardAssignedToGate(
                                eq(guard),
                                eq(entryGate),
                                any(LocalDateTime.class)))
                                .thenReturn(true);

                when(entryExitLogRepository.findByUserAndStatus(
                                user, MovementStatus.IN))
                                .thenReturn(Optional.empty());

                EntryExitResponse response = entryExitService.markEntry(entryRequest, guardId);

                assertThat(response.getStatus()).isEqualTo(MovementStatus.IN);
                assertThat(response.getEntryGateName()).isEqualTo("Main Gate");
                assertThat(response.getExitGateName()).isNull();

                verify(entryExitLogRepository).save(any(EntryExitLog.class));
        }

        @Test
        void shouldRejectEntry_whenUserIsAlreadyInside() {

                EntryExitLog activeLog = new EntryExitLog();
                activeLog.setStatus(MovementStatus.IN);

                when(userRepository.findById(user.getId()))
                                .thenReturn(Optional.of(user));

                when(userRepository.findById(guardId))
                                .thenReturn(Optional.of(guard));

                when(gateRepository.findById(entryGate.getId()))
                                .thenReturn(Optional.of(entryGate));

                when(gateAssignmentRepository.isGuardAssignedToGate(
                                eq(guard),
                                eq(entryGate),
                                any(LocalDateTime.class)))
                                .thenReturn(true);

                when(entryExitLogRepository.findByUserAndStatus(
                                user, MovementStatus.IN))
                                .thenReturn(Optional.of(activeLog));

                assertThatThrownBy(() -> entryExitService.markEntry(entryRequest, guardId))
                                .isInstanceOf(IllegalStateException.class)
                                .hasMessage("User is already inside campus");

                verify(entryExitLogRepository, never()).save(any());
        }

        // ---------------- EXIT TESTS ----------------

        @Test
        void shouldAllowExit_whenUserIsInside() {

                EntryExitLog activeLog = new EntryExitLog();
                activeLog.setUser(user);
                activeLog.setEntryGate(entryGate);
                activeLog.setEntryTime(LocalDateTime.now());
                activeLog.setStatus(MovementStatus.IN);

                when(userRepository.findById(user.getId()))
                                .thenReturn(Optional.of(user));

                when(userRepository.findById(guardId))
                                .thenReturn(Optional.of(guard));

                when(gateRepository.findById(exitGate.getId()))
                                .thenReturn(Optional.of(exitGate));

                when(gateAssignmentRepository.isGuardAssignedToGate(
                                eq(guard),
                                eq(exitGate),
                                any(LocalDateTime.class)))
                                .thenReturn(true);

                when(entryExitLogRepository.findByUserAndStatus(
                                user, MovementStatus.IN))
                                .thenReturn(Optional.of(activeLog));

                EntryExitResponse response = entryExitService.markExit(exitRequest, guardId);

                assertThat(response.getStatus()).isEqualTo(MovementStatus.OUT);
                assertThat(response.getExitGateName()).isEqualTo("Back Gate");

                verify(entryExitLogRepository).save(activeLog);
        }

        @Test
        void shouldRejectExit_whenUserIsOutside() {

                when(userRepository.findById(user.getId()))
                                .thenReturn(Optional.of(user));

                when(userRepository.findById(guardId))
                                .thenReturn(Optional.of(guard));

                assertThatThrownBy(() -> entryExitService.markExit(exitRequest, guardId))
                                .isInstanceOf(IllegalArgumentException.class)
                                .hasMessage("Gate not found");
        }

        // ---------------- GUEST ENTRY TESTS ----------------

        @Test
        void shouldCreateEntryAndLinkGuestVisit_whenApprovedVisitExists() {

                EntryRequest guestEntry = new EntryRequest(guest.getId(), entryGate.getId());

                GuestVisit approvedVisit = new GuestVisit();
                approvedVisit.setGuest(guest);
                approvedVisit.setExpectedGate(entryGate);
                approvedVisit.setStatus(GuestVisitStatus.APPROVED);

                when(userRepository.findById(guest.getId()))
                                .thenReturn(Optional.of(guest));

                when(userRepository.findById(guardId))
                                .thenReturn(Optional.of(guard));

                when(gateRepository.findById(entryGate.getId()))
                                .thenReturn(Optional.of(entryGate));

                when(gateAssignmentRepository.isGuardAssignedToGate(
                                eq(guard),
                                eq(entryGate),
                                any(LocalDateTime.class)))
                                .thenReturn(true);

                when(entryExitLogRepository.findByUserAndStatus(
                                guest, MovementStatus.IN))
                                .thenReturn(Optional.empty());

                when(guestVisitRepository.findByGuestAndStatus(
                                guest, GuestVisitStatus.APPROVED))
                                .thenReturn(Optional.of(approvedVisit));

                entryExitService.markEntry(guestEntry, guardId);

                assertThat(approvedVisit.getStatus())
                                .isEqualTo(GuestVisitStatus.ENTERED);

                verify(entryExitLogRepository).save(any(EntryExitLog.class));
                verify(guestVisitRepository).save(approvedVisit);
        }

        @Test
        void shouldRejectGuestEntry_whenNoApprovedVisitExists() {

                EntryRequest guestEntry = new EntryRequest(guest.getId(), entryGate.getId());

                when(userRepository.findById(guest.getId()))
                                .thenReturn(Optional.of(guest));

                when(userRepository.findById(guardId))
                                .thenReturn(Optional.of(guard));

                when(gateRepository.findById(entryGate.getId()))
                                .thenReturn(Optional.of(entryGate));

                when(gateAssignmentRepository.isGuardAssignedToGate(
                                eq(guard),
                                eq(entryGate),
                                any(LocalDateTime.class)))
                                .thenReturn(true);

                when(entryExitLogRepository.findByUserAndStatus(
                                guest, MovementStatus.IN))
                                .thenReturn(Optional.empty());

                when(guestVisitRepository.findByGuestAndStatus(
                                guest, GuestVisitStatus.APPROVED))
                                .thenReturn(Optional.empty());

                assertThatThrownBy(() -> entryExitService.markEntry(guestEntry, guardId))
                                .isInstanceOf(IllegalStateException.class)
                                .hasMessage("No approved guest visit found");

                verify(entryExitLogRepository).save(any(EntryExitLog.class));
                verify(guestVisitRepository, never()).save(any());
        }
}
