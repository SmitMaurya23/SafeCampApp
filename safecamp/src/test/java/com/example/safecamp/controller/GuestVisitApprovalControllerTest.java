package com.example.safecamp.controller;

import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.safecamp.dto.GuestVisitResponse;
import com.example.safecamp.enums.GuestVisitStatus;
import com.example.safecamp.service.GuestVisitApprovalService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(GuestVisitApprovalController.class)
@AutoConfigureMockMvc(addFilters = false) // disable security filters (future-proof)
public class GuestVisitApprovalControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private GuestVisitApprovalService approvalService;
    private UUID visitId;
    private UUID approverId;
    private GuestVisitResponse response;

    @BeforeEach
    void setup() {
        visitId = UUID.randomUUID();
        approverId = UUID.randomUUID();

        response = new GuestVisitResponse();
        response.setVisitId(visitId);
        response.setStatus(GuestVisitStatus.APPROVED);
    }

    @Test
    void shouldApproveGuestVisitSuccessfully() throws Exception {

        when(approvalService.approveVisit(visitId, approverId))
                .thenReturn(response);

        mockMvc.perform(post("/api/guest-visits/{visitId}/approve", visitId)
                .param("approverId", approverId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.visitId").value(visitId.toString()))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void shouldReturnError_whenServiceThrowsException() throws Exception {

        when(approvalService.approveVisit(visitId, approverId))
                .thenThrow(new IllegalStateException("Only security can approve"));

        mockMvc.perform(post("/api/guest-visits/{visitId}/approve", visitId)
                .param("approverId", approverId.toString()))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldRejectGuestVisitSuccessfully() throws Exception {

        response.setStatus(GuestVisitStatus.REJECTED);

        when(approvalService.rejectVisit(visitId, approverId))
                .thenReturn(response);

        mockMvc.perform(post("/api/guest-visits/{visitId}/reject", visitId)
                .param("approverId", approverId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }

}
