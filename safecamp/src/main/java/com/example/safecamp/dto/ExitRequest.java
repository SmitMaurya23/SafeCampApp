package com.example.safecamp.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExitRequest {
    @NotNull
    private UUID userId;
    @NotNull
    private UUID exitGateId;
}

