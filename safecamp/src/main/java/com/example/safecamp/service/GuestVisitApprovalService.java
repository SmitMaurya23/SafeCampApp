package com.example.safecamp.service;

import java.util.UUID;

import com.example.safecamp.dto.GuestVisitResponse;

public interface GuestVisitApprovalService {

    GuestVisitResponse approveVisit(UUID visitId, UUID approverId);

    GuestVisitResponse rejectVisit(UUID visitId, UUID approverId);
}
