package com.example.safecamp.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.safecamp.dto.CreateGuestVisitRequest;
import com.example.safecamp.entity.Gate;
import com.example.safecamp.entity.User;
import com.example.safecamp.enums.Role;
import com.example.safecamp.repository.GateRepository;
import com.example.safecamp.repository.GuestVisitRepository;
import com.example.safecamp.repository.UserRepository;
import com.example.safecamp.service.serviceImpl.GuestVisitServiceImpl;

@ExtendWith(MockitoExtension.class)
public class GuestVisitServiceImplTest {
        @Mock
        private GuestVisitRepository guestVisitRepository;

        @Mock
        private UserRepository userRepository;

        @Mock
        private GateRepository gateRepository;

        @InjectMocks
        private GuestVisitServiceImpl guestVisitService;

        private User hostResident;
        private User guest;
        private Gate gate;
        private CreateGuestVisitRequest request;

        @BeforeEach
        void setup() {
                hostResident = new User();
                hostResident.setId(UUID.randomUUID());
                hostResident.setName("Amit");
                hostResident.setRole(Role.RESIDENT);

                guest = new User();
                guest.setId(UUID.randomUUID());
                guest.setName("Rahul");
                guest.setPhone("7364510928");
                guest.setRole(Role.GUEST);

                gate = new Gate();
                gate.setId(UUID.randomUUID());
                gate.setName("Main Gate");

                request = new CreateGuestVisitRequest(
                                guest.getId(),
                                gate.getId(),
                                LocalDateTime.now().plusHours(1));
        }

        @Test
        void shouldRejectGuestVisit_whenHostIsNotResident() {

                hostResident.setRole(Role.SECURITY);

                when(userRepository.findById(hostResident.getId()))
                                .thenReturn(Optional.of(hostResident));

                assertThatThrownBy(() -> guestVisitService.createGuestVisit(request, hostResident.getId()))
                                .isInstanceOf(IllegalStateException.class)
                                .hasMessage("Only residents can create guest visits");
        }

        @Test
        void shouldFail_whenGateNotFound() {

                when(userRepository.findById(hostResident.getId()))
                                .thenReturn(Optional.of(hostResident));

                when(userRepository.findById(guest.getId()))
                                .thenReturn(Optional.of(guest));

                when(gateRepository.findById(gate.getId()))
                                .thenReturn(Optional.empty());

                assertThatThrownBy(() -> guestVisitService.createGuestVisit(request, hostResident.getId()))
                                .isInstanceOf(IllegalArgumentException.class)
                                .hasMessage("Gate not found");
        }

}
