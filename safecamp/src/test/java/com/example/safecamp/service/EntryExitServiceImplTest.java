package com.example.safecamp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.safecamp.dto.EntryExitRequest;
import com.example.safecamp.dto.EntryExitResponse;
import com.example.safecamp.entity.EntryExitLog;
import com.example.safecamp.entity.Gate;
import com.example.safecamp.entity.GuestVisit;
import com.example.safecamp.entity.User;
import com.example.safecamp.enums.GuestVisitStatus;
import com.example.safecamp.enums.MovementStatus;
import com.example.safecamp.enums.Role;
import com.example.safecamp.repository.EntryExitLogRepository;
import com.example.safecamp.repository.GateRepository;
import com.example.safecamp.repository.GuestVisitRepository;
import com.example.safecamp.repository.UserRepository;
import com.example.safecamp.service.serviceImpl.EntryExitServiceImpl;

@ExtendWith(MockitoExtension.class)
public class EntryExitServiceImplTest {

        @Mock
        private UserRepository userRepository;

        @Mock
        private GateRepository gateRepository;

        @Mock
        private EntryExitLogRepository entryExitLogRepository;

        @Mock
        private GuestVisitRepository guestVisitRepository;

        @InjectMocks
        private EntryExitServiceImpl entryExitService;

        private User user;
        private User guest;
        private Gate gate;
        private EntryExitRequest request;
        private EntryExitRequest guestRequest;

        @BeforeEach
        void setup() {
                user = new User();
                user.setId(UUID.randomUUID());
                user.setName("Amit");

                gate = new Gate();
                gate.setId(UUID.randomUUID());
                gate.setName("Main Gate");

                request = new EntryExitRequest(user.getId(), gate.getId());

                guest = new User();
                guest.setId(UUID.randomUUID());
                guest.setName("Rahul");
                guest.setRole(Role.GUEST);

                guestRequest = new EntryExitRequest(
                                guest.getId(),
                                gate.getId());

        }

        @Test
        void shouldAllowEntry_whenUserIsOutside() {
                when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

                when(gateRepository.findById(gate.getId())).thenReturn(Optional.of(gate));

                when(entryExitLogRepository.findTopByUserAndStatusOrderByEntryTimeDesc(user, MovementStatus.IN))
                                .thenReturn(Optional.empty());

                EntryExitResponse response = entryExitService.markEntry(request);

                assertThat(response.getStatus()).isEqualTo(MovementStatus.IN);
                assertThat(response.getUserId()).isEqualTo(user.getId());

                verify(entryExitLogRepository).save(any(EntryExitLog.class));
        }

        @Test
        void shouldRejectEntry_whenUserIsAlreadyInside() {

                EntryExitLog existingLog = new EntryExitLog();
                existingLog.setStatus(MovementStatus.IN);

                when(userRepository.findById(user.getId()))
                                .thenReturn(Optional.of(user));

                when(gateRepository.findById(gate.getId()))
                                .thenReturn(Optional.of(gate));

                when(entryExitLogRepository
                                .findTopByUserAndStatusOrderByEntryTimeDesc(user, MovementStatus.IN))
                                .thenReturn(Optional.of(existingLog));

                assertThatThrownBy(() -> entryExitService.markEntry(request))
                                .isInstanceOf(IllegalStateException.class)
                                .hasMessage("User is already inside campus");
        }

        @Test
        void shouldAllowExit_whenUserIsInside() {

                EntryExitLog activeLog = new EntryExitLog();
                activeLog.setStatus(MovementStatus.IN);
                activeLog.setGate(gate);

                when(userRepository.findById(user.getId()))
                                .thenReturn(Optional.of(user));

                when(entryExitLogRepository
                                .findTopByUserAndStatusOrderByEntryTimeDesc(user, MovementStatus.IN))
                                .thenReturn(Optional.of(activeLog));

                EntryExitResponse response = entryExitService.markExit(request);

                assertThat(response.getStatus()).isEqualTo(MovementStatus.OUT);
                assertThat(response.getGateName()).isEqualTo(gate.getName());

                verify(entryExitLogRepository).save(activeLog);
        }

        @Test
        void shouldRejectExit_whenUserIsOutside() {

                when(userRepository.findById(user.getId()))
                                .thenReturn(Optional.of(user));

                when(entryExitLogRepository
                                .findTopByUserAndStatusOrderByEntryTimeDesc(user, MovementStatus.IN))
                                .thenReturn(Optional.empty());

                assertThatThrownBy(() -> entryExitService.markExit(request))
                                .isInstanceOf(IllegalStateException.class)
                                .hasMessage("User is not inside campus");
        }

        // FOR GUESTS
        @Test
        void shouldCreateEntryAndLinkGuestVisit_whenPendingVisitExists() {

                GuestVisit pendingVisit = new GuestVisit();
                pendingVisit.setGuest(guest);
                pendingVisit.setStatus(GuestVisitStatus.PENDING);

                when(userRepository.findById(guest.getId()))
                                .thenReturn(Optional.of(guest));

                when(gateRepository.findById(gate.getId()))
                                .thenReturn(Optional.of(gate));

                when(guestVisitRepository.findByGuestAndStatus(
                                guest, GuestVisitStatus.PENDING))
                                .thenReturn(Optional.of(pendingVisit));

                entryExitService.markEntry(guestRequest);

                verify(entryExitLogRepository).save(any(EntryExitLog.class));
                verify(guestVisitRepository).save(pendingVisit);

                assertThat(pendingVisit.getStatus())
                                .isEqualTo(GuestVisitStatus.ENTERED);
        }

        @Test
        void shouldRejectEntryAndNotPersistLog_whenGuestHasNoPendingVisit() {

                when(userRepository.findById(guest.getId()))
                                .thenReturn(Optional.of(guest));

                when(gateRepository.findById(gate.getId()))
                                .thenReturn(Optional.of(gate));

                when(entryExitLogRepository.save(any()))
                                .thenAnswer(inv -> inv.getArgument(0));

                when(guestVisitRepository.findByGuestAndStatus(
                                guest, GuestVisitStatus.PENDING))
                                .thenReturn(Optional.empty());

                assertThatThrownBy(() -> entryExitService.markEntry(guestRequest))
                                .isInstanceOf(IllegalStateException.class)
                                .hasMessage("No pending guest visit found");

                verify(entryExitLogRepository).save(any());
                verify(guestVisitRepository, never()).save(any());
        }

}
