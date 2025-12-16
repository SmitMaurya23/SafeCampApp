package com.example.safecamp.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.safecamp.enums.MovementStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntryExitResponse {

    private UUID userId;
    private String userName;

    private String entryGateName;
    private LocalDateTime entryTime;

    private String exitGateName;
    private LocalDateTime exitTime;

    private MovementStatus status;
}
