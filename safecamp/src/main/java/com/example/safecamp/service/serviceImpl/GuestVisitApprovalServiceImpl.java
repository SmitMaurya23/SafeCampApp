package com.example.safecamp.service.serviceImpl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.safecamp.dto.GuestVisitResponse;
import com.example.safecamp.entity.EntryExitLog;
import com.example.safecamp.entity.Gate;
import com.example.safecamp.entity.GuestVisit;
import com.example.safecamp.entity.User;
import com.example.safecamp.enums.GuestVisitStatus;
import com.example.safecamp.repository.GateAssignmentRepository;
import com.example.safecamp.repository.GuestVisitRepository;
import com.example.safecamp.repository.UserRepository;
import com.example.safecamp.service.GuestVisitApprovalService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class GuestVisitApprovalServiceImpl
        implements GuestVisitApprovalService {

    private final GuestVisitRepository guestVisitRepository;
    private final UserRepository userRepository;
    private final GateAssignmentRepository gateAssignmentRepository;

    @Override
    public GuestVisitResponse approveVisit(UUID visitId, UUID approverId) {

        User approver = userRepository.findById(approverId)
                .orElseThrow(() -> new IllegalArgumentException("Approver not found"));


        GuestVisit visit = guestVisitRepository.findById(visitId)
                .orElseThrow(() -> new IllegalArgumentException("Guest visit not found"));

        Gate gate=visit.getExpectedGate();

        if (!gateAssignmentRepository.isGuardAssignedToGate(
                approver,
                gate,
                LocalDateTime.now())) {
            throw new IllegalStateException("Guard not assigned to visit gate");
        }

        if (visit.getStatus() != GuestVisitStatus.PENDING) {
            throw new IllegalStateException("Only pending visits can be approved");
        }

        visit.setStatus(GuestVisitStatus.APPROVED);
        visit.setCheckedBy(approver);

        GuestVisit saved = guestVisitRepository.save(visit);

        EntryExitLog log = saved.getEntryExitLog();

        return new GuestVisitResponse(
                saved.getId(),

                saved.getGuest().getId(),
                saved.getGuest().getName(),
                saved.getGuest().getPhone(),

                saved.getHostResident().getId(),
                saved.getHostResident().getName(),

                saved.getExpectedGate().getId(),
                saved.getExpectedGate().getName(),
                saved.getExpectedEntryTime(),

                log != null ? log.getId() : null,
                log != null ? log.getEntryTime() : null,
                log != null ? log.getExitTime() : null,

                saved.getStatus());

    }

    @Override
    public GuestVisitResponse rejectVisit(UUID visitId, UUID approverId) {

        User approver = userRepository.findById(approverId)
                .orElseThrow(() -> new IllegalArgumentException("Approver not found"));

        GuestVisit visit = guestVisitRepository.findById(visitId)
                .orElseThrow(() -> new IllegalArgumentException("Guest visit not found"));

        Gate gate=visit.getExpectedGate();


        if (!gateAssignmentRepository.isGuardAssignedToGate(
                approver,
                gate,
                LocalDateTime.now())) {
            throw new IllegalStateException("Guard not assigned to visit gate");
        }


        if (visit.getStatus() != GuestVisitStatus.PENDING) {
            throw new IllegalStateException("Only pending visits can be rejected");
        }

        visit.setStatus(GuestVisitStatus.REJECTED);
        visit.setCheckedBy(approver);

        GuestVisit saved = guestVisitRepository.save(visit);

        EntryExitLog log = saved.getEntryExitLog();

        return new GuestVisitResponse(
                saved.getId(),

                saved.getGuest().getId(),
                saved.getGuest().getName(),
                saved.getGuest().getPhone(),

                saved.getHostResident().getId(),
                saved.getHostResident().getName(),

                saved.getExpectedGate().getId(),
                saved.getExpectedGate().getName(),
                saved.getExpectedEntryTime(),

                log != null ? log.getId() : null,
                log != null ? log.getEntryTime() : null,
                log != null ? log.getExitTime() : null,

                saved.getStatus());

    }

}