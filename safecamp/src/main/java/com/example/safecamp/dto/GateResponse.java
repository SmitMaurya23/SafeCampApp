package com.example.safecamp.dto;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GateResponse {
    private UUID id;
    private String name;
    private String location;
}
