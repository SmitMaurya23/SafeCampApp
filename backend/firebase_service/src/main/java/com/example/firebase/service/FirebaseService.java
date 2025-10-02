package com.example.firebase.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.UUID;

@Service
public class FirebaseService {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseService.class);

    public void saveUser(Map<String, Object> userData) {
        Firestore db = FirestoreClient.getFirestore();
        String email = (String) userData.get("email");
        logger.info("Saving user to Firestore: {}", email);

        db.collection("pending-users").document(email).set(userData);

    }

    public Map<String, Object> login(String email, String password) {
        try {
            logger.info("Login attempt for email: {}", email);
            Firestore db = FirestoreClient.getFirestore();
            DocumentSnapshot snapshot = db.collection("users").document(email).get().get();

            if (!snapshot.exists()) {
                throw new RuntimeException("User not found in Firestore");
            }

            Map<String, Object> userData = snapshot.getData();
            String storedPassword = (String) userData.get("password");

            if (!password.equals(storedPassword)) {
                throw new RuntimeException("Invalid password");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("role", userData.get("role"));
            response.put("approved", userData.get("approved"));
            logger.info("Login successful for email: {}", email);
            return response;

        } catch (Exception e) {
            throw new RuntimeException("Login failed: " + e.getMessage());
        }
    }

    public Map<String, Object> getUserByEmail(String email) {
        try {
            logger.info("Fetching user by email: {}", email);
            Firestore db = FirestoreClient.getFirestore();
            DocumentSnapshot snapshot = db.collection("users").document(email).get().get();
            return snapshot.getData();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch user: " + e.getMessage());
        }
    }

    public void approveUser(String email) throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();

        // 🔍 Check if user already exists
        DocumentSnapshot existingUser = db.collection("users").document(email).get().get();
        if (existingUser.exists()) {
            throw new RuntimeException("User with email " + email + " is already registered");
        }

        // 🔍 Check if user is pending
        DocumentSnapshot pendingSnapshot = db.collection("pending-users").document(email).get().get();
        if (!pendingSnapshot.exists()) {
            throw new RuntimeException("User not found in pending-users");
        }

        Map<String, Object> userData = pendingSnapshot.getData();
        userData.put("approved", true);

        // 🔑 Assign unique ID
        String userId = UUID.randomUUID().toString();
        userData.put("id", userId);

        // ✅ Move to users collection
        db.collection("users").document(email).set(userData);
        db.collection("pending-users").document(email).delete();

        logger.info("User approved: {} with ID: {}", email, userId);
    }

    public void rejectUser(String email) {
        Firestore db = FirestoreClient.getFirestore();
        db.collection("pending-users").document(email).delete();
    }

}
