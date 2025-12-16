package com.example.safecamp.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.safecamp.dto.CreateGuestVisitRequest;
import com.example.safecamp.dto.GuestVisitResponse;
import com.example.safecamp.service.GuestVisitService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/guest-visits")
@RequiredArgsConstructor
public class GuestVisitController {

    private final GuestVisitService guestVisitService;

    @PostMapping("/host/{hostResidentId}")
    public ResponseEntity<GuestVisitResponse> createGuestVisit(
            @PathVariable UUID hostResidentId,
            @Valid @RequestBody CreateGuestVisitRequest request) {

        GuestVisitResponse response = guestVisitService.createGuestVisit(request, hostResidentId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
