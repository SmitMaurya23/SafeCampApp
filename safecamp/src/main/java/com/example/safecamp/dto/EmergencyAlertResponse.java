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
public class EmergencyAlertResponse {
    private UUID alertId;

    private UUID userId;
    private String userName;
    private String userPhone;

    private Double latitude;
    private Double longitude;

    private EmergencyStatus status;

    private LocalDateTime createdAt;

}
