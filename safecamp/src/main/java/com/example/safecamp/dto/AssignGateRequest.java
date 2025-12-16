package com.example.safecamp.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignGateRequest {

    @NotNull
    private UUID guardId;

    @NotNull
    private UUID gateId;

    @NotNull
    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
