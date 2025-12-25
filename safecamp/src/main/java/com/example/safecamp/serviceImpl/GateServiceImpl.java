package com.example.safecamp.serviceImpl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.safecamp.dto.CreateGateRequest;
import com.example.safecamp.dto.GateResponse;
import com.example.safecamp.entity.Gate;
import com.example.safecamp.repository.GateRepository;
import com.example.safecamp.service.GateService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class GateServiceImpl implements GateService {

    private final GateRepository gateRepository;

    @Override
    public GateResponse createGate(CreateGateRequest request) {
        if (gateRepository.existsByName(request.getName()) || gateRepository.existsByLocation(request.getLocation())) {
            throw new IllegalArgumentException("Gate already exists!!!");
        }
        Gate gate = Gate.builder()
                        .name(request.getName())
                        .location(request.getLocation())
                        .build();

        Gate savedGate = gateRepository.save(gate);

        return GateResponse.builder()
                            .id(savedGate.getId())
                            .name(savedGate.getName())
                            .location(savedGate.getLocation())
                            .build();
    }

    @Override
    public GateResponse getGateById(UUID id) {
        Gate gate=gateRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Gate does not exists!!!"));

        return GateResponse.builder()
                            .id(gate.getId())
                            .name(gate.getName())
                            .location(gate.getLocation())
                            .build();
    }
}
