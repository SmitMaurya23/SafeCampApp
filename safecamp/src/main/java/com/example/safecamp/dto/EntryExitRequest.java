package com.example.safecamp.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntryExitRequest {
    @NotNull
    private UUID userId;
    @NotNull
    private UUID gateId;
}
