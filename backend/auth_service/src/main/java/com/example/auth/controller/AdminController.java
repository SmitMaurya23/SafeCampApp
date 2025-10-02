package com.example.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.auth.service.AdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/approve/{email}")
    public ResponseEntity<String> approveUser(@PathVariable String email) {
        adminService.approveUser(email);
        return ResponseEntity.ok("User approved and moved to users collection");
    }

    @DeleteMapping("/reject/{email}")
    public ResponseEntity<String> rejectUser(@PathVariable String email) {
        adminService.rejectUser(email);
        return ResponseEntity.ok("User rejected and removed from pending-users");
    }
}
