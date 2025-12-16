package com.example.safecamp.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GateAssignmentResponse {

    private UUID assignmentId;

    private UUID guardId;
    private String guardName;

    private UUID gateId;
    private String gateName;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
}

