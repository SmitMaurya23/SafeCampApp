package com.example.safecamp.service;

import com.example.safecamp.dto.EntryExitRequest;
import com.example.safecamp.dto.EntryExitResponse;

public interface EntryExitService {
    EntryExitResponse markEntry(EntryExitRequest request);
    EntryExitResponse markExit(EntryExitRequest request);
}
