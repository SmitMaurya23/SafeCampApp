package com.example.safecamp.service;

import java.util.UUID;

import com.example.safecamp.dto.CreateGuestVisitRequest;
import com.example.safecamp.dto.GuestVisitResponse;

public interface GuestVisitService {

    GuestVisitResponse createGuestVisit(
            CreateGuestVisitRequest request,
            UUID hostResidentId);
}
