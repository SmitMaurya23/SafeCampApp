package com.example.safecamp.service.serviceImpl;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

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
import com.example.safecamp.service.EntryExitService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor

public class EntryExitServiceImpl implements EntryExitService {
    private final UserRepository userRepository;
    private final GateRepository gateRepository;
    private final EntryExitLogRepository entryExitLogRepository;
    private final GuestVisitRepository guestVisitRepository;

    public EntryExitResponse markEntry(EntryExitRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Gate gate = gateRepository.findById(request.getGateId())
                .orElseThrow(() -> new IllegalArgumentException("Gate not found"));

        Optional<EntryExitLog> activeEntry = entryExitLogRepository.findTopByUserAndStatusOrderByEntryTimeDesc(user,
                MovementStatus.IN);

        if (activeEntry.isPresent()) {
            throw new IllegalStateException("User is already inside campus");
        }

        EntryExitLog log = new EntryExitLog();
        log.setUser(user);
        log.setGate(gate);
        log.setEntryTime(LocalDateTime.now());
        log.setStatus(MovementStatus.IN);

        entryExitLogRepository.save(log);

        // 2️⃣ If subject is GUEST → link GuestVisit
        if (user.getRole() == Role.GUEST) {

            GuestVisit visit = guestVisitRepository
                    .findByGuestAndStatus(user, GuestVisitStatus.APPROVED)
                    .orElseThrow(() -> new IllegalStateException("No pending guest visit found"));

            visit.setEntryExitLog(log);
            visit.setStatus(GuestVisitStatus.ENTERED);

            guestVisitRepository.save(visit);
        }

        return new EntryExitResponse(
                user.getId(),
                user.getName(),
                gate.getName(),
                MovementStatus.IN,
                log.getEntryTime());
    }

    public EntryExitResponse markExit(EntryExitRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        EntryExitLog activeEntry = entryExitLogRepository
                .findTopByUserAndStatusOrderByEntryTimeDesc(user, MovementStatus.IN)
                .orElseThrow(() -> new IllegalStateException("User is not inside campus"));

        activeEntry.setExitTime(LocalDateTime.now());
        activeEntry.setStatus(MovementStatus.OUT);
        entryExitLogRepository.save(activeEntry);

        // 2️⃣ If subject is GUEST → link GuestVisit
        if (user.getRole() == Role.GUEST) {

            GuestVisit visit = guestVisitRepository
                    .findByGuestAndStatus(user, GuestVisitStatus.ENTERED)
                    .orElseThrow(() -> new IllegalStateException("Guest visit not active"));

            visit.setStatus(GuestVisitStatus.EXITED);
            guestVisitRepository.save(visit);
        }

        return new EntryExitResponse(user.getId(),
                user.getName(),
                activeEntry.getGate().getName(),
                MovementStatus.OUT,
                activeEntry.getExitTime());
    }
}
