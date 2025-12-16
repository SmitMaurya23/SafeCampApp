package com.example.safecamp.service.serviceImpl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.safecamp.dto.CreateGateRequest;
import com.example.safecamp.dto.GateResponse;
import com.example.safecamp.entity.Gate;
import com.example.safecamp.repository.GateRepository;
import com.example.safecamp.service.GateService;

import jakarta.transaction.Transactional;
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
        Gate gate = new Gate();
        gate.setName(request.getName());
        gate.setLocation(request.getLocation());

        Gate savedGate = gateRepository.save(gate);

        GateResponse gateResponse = new GateResponse();
        gateResponse.setId(savedGate.getId());
        gateResponse.setName(savedGate.getName());
        gateResponse.setLocation(savedGate.getLocation());

        return gateResponse;
    }

    @Override
    public GateResponse getGateById(UUID id) {
        Gate gate=gateRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Gate does not exists!!!"));
        GateResponse gateResponse = new GateResponse();
        gateResponse.setId(gate.getId());
        gateResponse.setName(gate.getName());
        gateResponse.setLocation(gate.getLocation());

        return gateResponse;
    }
}
