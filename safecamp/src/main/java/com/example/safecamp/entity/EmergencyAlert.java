package com.example.safecamp.entity;

import java.time.LocalDateTime;

import com.example.safecamp.entity.base.BaseEntity;
import com.example.safecamp.enums.EmergencyStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EmergencyAlert extends BaseEntity {
    @ManyToOne(fetch=FetchType.LAZY, optional=false)

    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @Column(nullable=false)
    private Double latitude;

    @Column(nullable=false)
    private Double longitude;

    @Column(length=255)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private EmergencyStatus status;

    private LocalDateTime resolvedAt;
}
