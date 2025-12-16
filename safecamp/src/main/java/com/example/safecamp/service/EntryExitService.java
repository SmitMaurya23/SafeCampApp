package com.example.safecamp.service;

import java.util.UUID;

import com.example.safecamp.dto.EntryExitResponse;
import com.example.safecamp.dto.EntryRequest;
import com.example.safecamp.dto.ExitRequest;

public interface EntryExitService {
    EntryExitResponse markEntry(EntryRequest request, UUID guardId);
    EntryExitResponse markExit(ExitRequest request, UUID guardId);
}
