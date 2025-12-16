package com.example.safecamp.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.safecamp.dto.EntryExitRequest;
import com.example.safecamp.dto.EntryExitResponse;
import com.example.safecamp.enums.MovementStatus;
import com.example.safecamp.service.EntryExitService;

import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(EntryExitController.class)
public class EntryExitControllerTest {
        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private EntryExitService entryExitService;

        @Autowired
        private ObjectMapper objectMapper;

        private EntryExitRequest request;
        private UUID userId;
        private UUID gateId;

        @BeforeEach
        void setup() {
                userId = UUID.randomUUID();
                gateId = UUID.randomUUID();
                request = new EntryExitRequest(userId, gateId);
        }

        @Test
        void shouldMarkEntrySuccessfully() throws Exception {

                EntryExitResponse response = new EntryExitResponse(
                                userId,
                                "Amit",
                                "Main Gate",
                                MovementStatus.IN,
                                LocalDateTime.now());

                when(entryExitService.markEntry(any()))
                                .thenReturn(response);

                mockMvc.perform(post("/api/entry-exit/entry")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.status").value("IN"))
                                .andExpect(jsonPath("$.userId").value(userId.toString()));
        }

        @Test
        void shouldReturnConflict_whenExitWithoutEntry() throws Exception {
                when(entryExitService.markExit(any()))
                                .thenThrow(new IllegalStateException("User is not inside campus"));

                mockMvc.perform(post("/api/entry-exit/exit")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isConflict())
                                .andExpect(jsonPath("$.message")
                                                .value("User is not inside campus"));
        }

        @Test
        void shouldReturnBadRequest_whenRequestIsInvalid() throws Exception {

                EntryExitRequest invalidRequest = new EntryExitRequest(null, gateId);

                mockMvc.perform(post("/api/entry-exit/entry")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                                .andExpect(status().isBadRequest());
        }

}
