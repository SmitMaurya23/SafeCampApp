package com.example.safecamp.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateGuestVisitRequest {

    @Valid
    @NotNull
    private UUID guestId;

    @NotNull
    private UUID expectedGateId;

    @NotNull
    private LocalDateTime expectedEntryTime;
}
