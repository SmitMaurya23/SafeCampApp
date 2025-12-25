package com.example.safecamp.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.safecamp.dto.CreateGateRequest;
import com.example.safecamp.dto.GateResponse;
import com.example.safecamp.entity.Gate;
import com.example.safecamp.repository.GateRepository;
import com.example.safecamp.serviceImpl.GateServiceImpl;

@ExtendWith(MockitoExtension.class)
class GateServiceImplTest {

    @Mock
    private GateRepository gateRepository;

    @InjectMocks
    private GateServiceImpl gateService;

    private CreateGateRequest request;
    private Gate gate;

    @BeforeEach
    void setUp() {
        request = new CreateGateRequest();
        request.setName("Main Gate");
        request.setLocation("North Block");

        gate = new Gate();
        gate.setId(UUID.randomUUID());
        gate.setName(request.getName());
        gate.setLocation(request.getLocation());
    }

    // ---------- createGate ----------

    @Test
    void createGate_success() {
        when(gateRepository.existsByName(request.getName())).thenReturn(false);
        when(gateRepository.existsByLocation(request.getLocation())).thenReturn(false);
        when(gateRepository.save(any(Gate.class))).thenReturn(gate);

        GateResponse response = gateService.createGate(request);

        assertNotNull(response);
        assertEquals(gate.getId(), response.getId());
        assertEquals(gate.getName(), response.getName());
        assertEquals(gate.getLocation(), response.getLocation());

        verify(gateRepository).save(any(Gate.class));
    }

    @Test
    void createGate_duplicateName_throwsException() {
        when(gateRepository.existsByName(request.getName())).thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> gateService.createGate(request)
        );

        assertEquals("Gate already exists!!!", ex.getMessage());

        verify(gateRepository, never()).save(any());
    }

    @Test
    void createGate_duplicateLocation_throwsException() {
        when(gateRepository.existsByName(request.getName())).thenReturn(false);
        when(gateRepository.existsByLocation(request.getLocation())).thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> gateService.createGate(request)
        );

        assertEquals("Gate already exists!!!", ex.getMessage());

        verify(gateRepository, never()).save(any());
    }

    // ---------- getGateById ----------

    @Test
    void getGateById_success() {
        UUID gateId = gate.getId();

        when(gateRepository.findById(gateId)).thenReturn(Optional.of(gate));

        GateResponse response = gateService.getGateById(gateId);

        assertNotNull(response);
        assertEquals(gateId, response.getId());
        assertEquals(gate.getName(), response.getName());
        assertEquals(gate.getLocation(), response.getLocation());
    }

    @Test
    void getGateById_notFound_throwsException() {
        UUID gateId = UUID.randomUUID();

        when(gateRepository.findById(gateId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> gateService.getGateById(gateId)
        );

        assertEquals("Gate does not exists!!!", ex.getMessage());
    }
}
