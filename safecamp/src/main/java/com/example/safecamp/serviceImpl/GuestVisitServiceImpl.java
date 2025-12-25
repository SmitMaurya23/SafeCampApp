package com.example.safecamp.serviceImpl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.example.safecamp.service.GuestVisitService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class GuestVisitServiceImpl implements GuestVisitService {

        private final GuestVisitRepository guestVisitRepository;
        private final UserRepository userRepository;
        private final GateRepository gateRepository;

        @Override
        public GuestVisitResponse createGuestVisit(CreateGuestVisitRequest request, UUID hostResidentId) {
                User host = userRepository.findById(hostResidentId)
                                .orElseThrow(() -> new IllegalArgumentException("Host resident not found"));

                User guest = userRepository.findById(request.getGuestId())
                                .orElseThrow(() -> new IllegalArgumentException("Guest not found"));

                // Checking Validity of a guest
                if (guest.getRole() != Role.GUEST) {
                        throw new IllegalStateException("Provided user is not a guest");
                }

                boolean hasActiveVisit = guestVisitRepository.existsByGuestAndStatusIn(
                                guest,
                                List.of(GuestVisitStatus.PENDING, GuestVisitStatus.APPROVED, GuestVisitStatus.ENTERED));

                if (hasActiveVisit) {
                        throw new IllegalStateException("Guest already has an active visit");
                }

                Gate gate = gateRepository.findById(request.getExpectedGateId())
                                .orElseThrow(() -> new IllegalArgumentException("Gate not found"));

                GuestVisit visit = GuestVisit.builder()
                                .checkedBy(guest)
                                .entryExitLog(null)
                                .expectedEntryTime(request.getExpectedEntryTime())
                                .expectedGate(gate)
                                .guest(null)
                                .hostResident(host)
                                .status(GuestVisitStatus.PENDING)
                                .build();

                GuestVisit saved = guestVisitRepository.save(visit);

                return GuestVisitResponse.builder()
                                .actualEntryTime(saved.getExpectedEntryTime())
                                .actualExitTime(null)
                                .entryExitLogId(hostResidentId)
                                .expectedEntryTime(null)
                                .expectedGateId(gate.getId())
                                .expectedGateName(gate.getName())
                                .guestId(guest.getId())
                                .guestName(guest.getName())
                                .guestPhone(guest.getPhone())
                                .hostResidentId(host.getId())
                                .hostResidentName(host.getName())
                                .status(saved.getStatus())
                                .visitId(saved.getId())
                                .build();
        }

}