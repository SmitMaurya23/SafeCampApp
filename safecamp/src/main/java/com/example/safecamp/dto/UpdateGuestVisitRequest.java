package com.example.safecamp.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.safecamp.enums.GuestVisitStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateGuestVisitRequest {

    private UUID expectedGateId;

    private LocalDateTime expectedEntryTime;

    private GuestVisitStatus status; // e.g. CANCELLED by host
}
