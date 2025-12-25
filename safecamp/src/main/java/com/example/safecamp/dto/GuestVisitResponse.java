package com.example.safecamp.dto;
import java.time.LocalDateTime;
import java.util.UUID;

import com.example.safecamp.enums.GuestVisitStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuestVisitResponse {

    private UUID visitId;

    // Guest info
    private UUID guestId;
    private String guestName;
    private String guestPhone;

    // Host info
    private UUID hostResidentId;
    private String hostResidentName;

    // Expected details
    private UUID expectedGateId;
    private String expectedGateName;
    private LocalDateTime expectedEntryTime;

    // Actual details (nullable until entry)
    private UUID entryExitLogId;
    private LocalDateTime actualEntryTime;
    private LocalDateTime actualExitTime;

    private GuestVisitStatus status;
}
