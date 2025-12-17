package com.example.safecamp.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.safecamp.dto.EntryExitResponse;
import com.example.safecamp.dto.EntryRequest;
import com.example.safecamp.dto.ExitRequest;
import com.example.safecamp.security.UserPrincipal;
import com.example.safecamp.service.EntryExitService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/entry-exit")
@RequiredArgsConstructor
public class EntryExitController {
    private final EntryExitService entryExitService;

    @PreAuthorize("hasRole('SECURITY')")
    @PostMapping("/entry")
    public ResponseEntity<EntryExitResponse> markEntry(@Valid @RequestBody EntryRequest request,
           @AuthenticationPrincipal UserPrincipal guard) {

        EntryExitResponse response = entryExitService.markEntry(request, guard.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('SECURITY')")
    @PostMapping("/exit")
    public ResponseEntity<EntryExitResponse> markExit(
            @Valid @RequestBody ExitRequest request, @AuthenticationPrincipal UserPrincipal guard) {

        EntryExitResponse response = entryExitService.markExit(request, guard.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}