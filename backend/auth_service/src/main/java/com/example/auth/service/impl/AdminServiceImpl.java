package com.example.auth.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import com.example.auth.service.AdminService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final RestTemplate restTemplate;

    @Value("${firebase.gateway.url}")
    private String firebaseGatewayUrl;

    @Override
    public void approveUser(String email) {
        String url = firebaseGatewayUrl + "/approve/" + email;
        log.info("Sending approval request to Firebase Gateway for email: {}", email);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);
            log.info("Approval response: {}", response.getBody());
        } catch (Exception e) {
            log.error("Error approving user {}: {}", email, e.getMessage());
            throw new RuntimeException("Approval failed for " + email + ": " + e.getMessage());
        }
    }

    @Override
    public void rejectUser(String email) {
        String url = firebaseGatewayUrl + "/reject/" + email;
        log.info("Sending rejection request to Firebase Gateway for email: {}", email);

        try {
            restTemplate.delete(url);
            log.info("User rejected successfully: {}", email);
        } catch (Exception e) {
            log.error("Error rejecting user {}: {}", email, e.getMessage());
            throw new RuntimeException("Rejection failed for " + email + ": " + e.getMessage());
        }
    }
}
