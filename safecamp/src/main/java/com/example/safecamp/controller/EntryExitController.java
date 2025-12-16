package com.example.safecamp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.safecamp.dto.EntryExitRequest;
import com.example.safecamp.dto.EntryExitResponse;
import com.example.safecamp.service.EntryExitService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/entry-exit")
@RequiredArgsConstructor
public class EntryExitController {
    private final EntryExitService entryExitService;

    @PostMapping("/entry")
    public ResponseEntity<EntryExitResponse> markEntry(@Valid @RequestBody EntryExitRequest request){

        EntryExitResponse response=entryExitService.markEntry(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/exit")
    public ResponseEntity<EntryExitResponse> markExit(
            @Valid @RequestBody EntryExitRequest request) {

        EntryExitResponse response = entryExitService.markExit(request);
        return ResponseEntity.ok(response);
    }

}
