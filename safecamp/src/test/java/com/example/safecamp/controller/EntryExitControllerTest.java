package com.example.safecamp.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.safecamp.dto.EntryExitResponse;
import com.example.safecamp.dto.EntryRequest;
import com.example.safecamp.dto.ExitRequest;
import com.example.safecamp.enums.MovementStatus;
import com.example.safecamp.exception.GlobalExceptionHandler;
import com.example.safecamp.service.EntryExitService;

import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(EntryExitController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class EntryExitControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private EntryExitService entryExitService;

        @Autowired
        private ObjectMapper objectMapper;

        private UUID userId;
        private UUID guardId;
        private UUID entryGateId;
        private UUID exitGateId;

        private EntryRequest entryRequest;
        private ExitRequest exitRequest;

        @BeforeEach
        void setup() {
                userId = UUID.randomUUID();
                guardId = UUID.randomUUID();
                entryGateId = UUID.randomUUID();
                exitGateId = UUID.randomUUID();

                entryRequest = new EntryRequest(userId, entryGateId);
                exitRequest = new ExitRequest(userId, exitGateId);
        }

        // ---------------- ENTRY ----------------

        @Test
        void shouldMarkEntrySuccessfully() throws Exception {

                EntryExitResponse response = new EntryExitResponse(
                                userId,
                                "Amit",
                                "Main Gate",
                                LocalDateTime.now(),
                                null,
                                null,
                                MovementStatus.IN);

                when(entryExitService.markEntry(any(EntryRequest.class), eq(guardId)))
                                .thenReturn(response);

                mockMvc.perform(post("/api/entry-exit/entry/{guardId}", guardId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(entryRequest)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.status").value("IN"))
                                .andExpect(jsonPath("$.userId").value(userId.toString()))
                                .andExpect(jsonPath("$.entryGateName").value("Main Gate"))
                                .andExpect(jsonPath("$.exitGateName").doesNotExist());
        }

        // ---------------- EXIT ----------------

        @Test
        void shouldReturnConflict_whenExitWithoutEntry() throws Exception {

                when(entryExitService.markExit(any(ExitRequest.class), eq(guardId)))
                                .thenThrow(new IllegalStateException("User is not inside campus"));

                mockMvc.perform(post("/api/entry-exit/exit/{guardId}", guardId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(exitRequest)))
                                .andExpect(status().isConflict())
                                .andExpect(jsonPath("$.message")
                                                .value("User is not inside campus"));
        }

        // ---------------- VALIDATION ----------------

        @Test
        void shouldReturnBadRequest_whenEntryRequestIsInvalid() throws Exception {

                EntryRequest invalidRequest = new EntryRequest(null, entryGateId);

                mockMvc.perform(post("/api/entry-exit/entry/{guardId}", guardId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturnBadRequest_whenExitRequestIsInvalid() throws Exception {

                ExitRequest invalidRequest = new ExitRequest(null, exitGateId);

                mockMvc.perform(post("/api/entry-exit/exit/{guardId}", guardId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                                .andExpect(status().isBadRequest());
        }
}
