package com.example.firebase.controller;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.firebase.service.FirebaseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/firebase")
@RequiredArgsConstructor
public class FirebaseController {

    private final FirebaseService firebaseService;

    @PostMapping("/save")
    public ResponseEntity<String> saveUser(@RequestBody Map<String, Object> userData) {
        firebaseService.saveUser(userData);
        return ResponseEntity.ok("User saved to Firestore");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        Map<String, Object> userData = firebaseService.login(credentials.get("email"), credentials.get("password"));
        return ResponseEntity.ok(userData);
    }

    @GetMapping("/user/{email}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable String email) {
        Map<String, Object> userData = firebaseService.getUserByEmail(email);
        return ResponseEntity.ok(userData);
    }

    @PostMapping("/approve/{email}")
    public ResponseEntity<String> approveUser(@PathVariable String email) throws InterruptedException, ExecutionException {
        firebaseService.approveUser(email);
        return ResponseEntity.ok("User approved");
    }

    @DeleteMapping("/reject/{email}")
    public ResponseEntity<String> rejectUser(@PathVariable String email) {
        firebaseService.rejectUser(email);
        return ResponseEntity.ok("User rejected");
    }

}
