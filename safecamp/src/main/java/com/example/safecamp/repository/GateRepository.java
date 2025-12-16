package com.example.safecamp.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.safecamp.entity.Gate;


public interface GateRepository extends JpaRepository<Gate,UUID>{
    boolean existsByName(String name);
    boolean existsByLocation(String location);
}
