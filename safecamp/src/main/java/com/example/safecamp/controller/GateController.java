package com.example.safecamp.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.safecamp.dto.CreateGateRequest;
import com.example.safecamp.dto.GateResponse;
import com.example.safecamp.service.GateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/gates")
@RequiredArgsConstructor
public class GateController {
    private final GateService gateService;

    @PostMapping
    public ResponseEntity<GateResponse> createGate(@Valid @RequestBody CreateGateRequest request) {
        GateResponse response = gateService.createGate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GateResponse> getGateById(@PathVariable UUID id) {
        GateResponse response = gateService.getGateById(id);
        return ResponseEntity.ok(response);
    }

}

