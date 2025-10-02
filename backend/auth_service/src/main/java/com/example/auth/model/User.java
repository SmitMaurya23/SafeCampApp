package com.example.auth.model;

import lombok.Data;

@Data
public class User {
    private String id;
    private String name;
    private String email;
    private String password;
    private String role;
    private boolean approved;
}
