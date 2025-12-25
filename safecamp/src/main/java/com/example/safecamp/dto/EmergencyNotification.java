package com.example.safecamp.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.safecamp.enums.EmergencyStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

//For WebSockets
public class EmergencyNotification {

    private UUID alertId;

    private String userName;
    private String userPhone;

    private Double latitude;
    private Double longitude;

    private String message;
    private EmergencyStatus status;

    private LocalDateTime timestamp;
}
