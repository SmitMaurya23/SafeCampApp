package com.example.admin.service;

public interface AdminService {
    void approveUser(String email);
    void rejectUser(String email);
}
