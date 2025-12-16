package com.example.safecamp.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
import com.example.safecamp.repository.GuestVisitRepository;
import com.example.safecamp.repository.UserRepository;
import com.example.safecamp.service.serviceImpl.GuestVisitApprovalServiceImpl;

@ExtendWith(MockitoExtension.class)
public class GuestVisitApprovalServiceImplTest {
    @Mock
    private GuestVisitRepository guestVisitRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GuestVisitApprovalServiceImpl approvalService;

    @Test
    void shouldApproveVisit_whenUserIsSecurity() {

        User security = new User();
        security.setId(UUID.randomUUID());
        security.setRole(Role.SECURITY);

        User guest = new User();
        guest.setId(UUID.randomUUID());
        guest.setName("Rahul");
        guest.setPhone("9876543210");
        guest.setRole(Role.GUEST);

        User host = new User();
        host.setId(UUID.randomUUID());
        host.setName("Amit");
        host.setRole(Role.RESIDENT);

        Gate gate = new Gate();
        gate.setId(UUID.randomUUID());
        gate.setName("Main Gate");

        GuestVisit visit = new GuestVisit();
        visit.setId(UUID.randomUUID());
        visit.setGuest(guest);
        visit.setHostResident(host);
        visit.setExpectedGate(gate);
        visit.setExpectedEntryTime(LocalDateTime.now());
        visit.setStatus(GuestVisitStatus.PENDING);

        when(userRepository.findById(security.getId()))
                .thenReturn(Optional.of(security));

        when(guestVisitRepository.findById(visit.getId()))
                .thenReturn(Optional.of(visit));

        when(guestVisitRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        GuestVisitResponse response = approvalService.approveVisit(visit.getId(), security.getId());

        assertThat(response.getStatus())
                .isEqualTo(GuestVisitStatus.APPROVED);
    }

    @Test
    void shouldRejectApproval_whenUserIsNotSecurity() {

        User resident = new User();
        resident.setId(UUID.randomUUID());
        resident.setRole(Role.RESIDENT);

        when(userRepository.findById(resident.getId()))
                .thenReturn(Optional.of(resident));

        assertThatThrownBy(() -> approvalService.approveVisit(UUID.randomUUID(), resident.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Only security can approve guest visits");
    }

    @Test
    void shouldRejectApproval_whenVisitNotPending() {

        User security = new User();
        security.setId(UUID.randomUUID());
        security.setRole(Role.SECURITY);

        GuestVisit visit = new GuestVisit();
        visit.setId(UUID.randomUUID());
        visit.setStatus(GuestVisitStatus.APPROVED);

        when(userRepository.findById(security.getId()))
                .thenReturn(Optional.of(security));

        when(guestVisitRepository.findById(visit.getId()))
                .thenReturn(Optional.of(visit));

        assertThatThrownBy(() -> approvalService.approveVisit(visit.getId(), security.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Only pending visits can be approved");
    }

}
