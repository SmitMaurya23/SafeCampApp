package com.example.safecamp.serviceImpl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.example.safecamp.service.EntryExitService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class EntryExitServiceImpl implements EntryExitService {

    private final UserRepository userRepository;
    private final GateRepository gateRepository;
    private final EntryExitLogRepository entryExitLogRepository;
    private final GuestVisitRepository guestVisitRepository;
    private final GateAssignmentRepository gateAssignmentRepository;

    @Override
    public EntryExitResponse markEntry(EntryRequest request, UUID guardId) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        User guard = userRepository.findById(guardId)
                .orElseThrow(() -> new IllegalArgumentException("Guard not found"));

        Gate entryGate = gateRepository.findById(request.getEntryGateId())
                .orElseThrow(() -> new IllegalArgumentException("Gate not found"));

        if (!gateAssignmentRepository.isGuardAssignedToGate(
                guard, entryGate, LocalDateTime.now())) {
            throw new IllegalStateException("Guard is not assigned to this entry gate");
        }

        if (entryExitLogRepository.findByUserAndStatus(user, MovementStatus.IN).isPresent()) {
            throw new IllegalStateException("User is already inside campus");
        }

        EntryExitLog log = EntryExitLog.builder()
                                        .user(user)
                                        .entryGate(entryGate)
                                        .entryGuard(guard)
                                        .entryTime(LocalDateTime.now())
                                        .status(MovementStatus.IN)
                                        .build();

        entryExitLogRepository.save(log);

        if (user.getRole() == Role.GUEST) {
            GuestVisit visit = guestVisitRepository
                    .findByGuestAndStatus(user, GuestVisitStatus.APPROVED)
                    .orElseThrow(() -> new IllegalStateException("No approved guest visit found"));

            visit.setEntryExitLog(log);
            visit.setStatus(GuestVisitStatus.ENTERED);
            guestVisitRepository.save(visit);
        }

        return EntryExitResponse.builder()
                                .userId(user.getId())
                                .userName(user.getName())
                                .entryGateName(entryGate.getName())
                                .entryTime(log.getEntryTime())
                                .exitGateName(null)
                                .exitTime(null)
                                .status(MovementStatus.IN)
                                .build();
    }

    @Override
    public EntryExitResponse markExit(ExitRequest request, UUID guardId) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        User guard = userRepository.findById(guardId)
                .orElseThrow(() -> new IllegalArgumentException("Guard not found"));

        Gate exitGate = gateRepository.findById(request.getExitGateId())
                .orElseThrow(() -> new IllegalArgumentException("Gate not found"));

        if (!gateAssignmentRepository.isGuardAssignedToGate(
                guard, exitGate, LocalDateTime.now())) {
            throw new IllegalStateException("Guard is not assigned to this exit gate");
        }

        EntryExitLog log = entryExitLogRepository
                .findByUserAndStatus(user, MovementStatus.IN)
                .orElseThrow(() -> new IllegalStateException("User is not inside campus"));

        log.setExitGate(exitGate);
        log.setExitGuard(guard);
        log.setExitTime(LocalDateTime.now());
        log.setStatus(MovementStatus.OUT);

        entryExitLogRepository.save(log);

        if (user.getRole() == Role.GUEST) {
            GuestVisit visit = guestVisitRepository
                    .findByGuestAndStatus(user, GuestVisitStatus.ENTERED)
                    .orElseThrow(() -> new IllegalStateException("Guest visit not active"));

            visit.setStatus(GuestVisitStatus.EXITED);
            guestVisitRepository.save(visit);
        }

        return EntryExitResponse.builder()
                                .userId(user.getId())
                                .userName(user.getName())
                                .entryGateName(log.getEntryGate().getName())
                                .entryTime(log.getEntryTime())
                                .exitGateName(exitGate.getName())
                                .exitTime( log.getExitTime())
                                .status(MovementStatus.OUT)
                                .build();
    }
}
