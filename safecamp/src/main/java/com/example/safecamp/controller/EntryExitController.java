package com.example.safecamp.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.safecamp.dto.EntryExitResponse;
import com.example.safecamp.dto.EntryRequest;
import com.example.safecamp.dto.ExitRequest;
import com.example.safecamp.service.EntryExitService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/entry-exit")
@RequiredArgsConstructor
public class EntryExitController {
    private final EntryExitService entryExitService;

    @PostMapping("/entry/{guardId}")
    public ResponseEntity<EntryExitResponse> markEntry(@Valid @RequestBody EntryRequest request,
            @PathVariable UUID guardId) {

        EntryExitResponse response = entryExitService.markEntry(request, guardId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/exit/{guardId}")
    public ResponseEntity<EntryExitResponse> markExit(
            @Valid @RequestBody ExitRequest request, @PathVariable UUID guardId) {

        EntryExitResponse response = entryExitService.markExit(request, guardId);
        return ResponseEntity.ok(response);
    }

}