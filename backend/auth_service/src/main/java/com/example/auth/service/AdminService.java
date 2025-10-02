package com.example.auth.service;

public interface AdminService {
    void approveUser(String email);

    void rejectUser(String email);
}
