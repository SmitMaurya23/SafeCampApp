package com.example.safecamp.entity;

import java.time.LocalDateTime;

import com.example.safecamp.entity.base.BaseEntity;
import com.example.safecamp.enums.MovementStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "entry_exit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EntryExitLog extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /* ---------- ENTRY EVENT ---------- */

    @ManyToOne(optional = false)
    @JoinColumn(name = "entry_gate_id", nullable = false)
    private Gate entryGate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "entry_guard_id", nullable = false)
    private User entryGuard;

    @Column(name = "entry_time", nullable = false)
    private LocalDateTime entryTime;

    /* ---------- EXIT EVENT ---------- */

    @ManyToOne
    @JoinColumn(name = "exit_gate_id")
    private Gate exitGate;

    @ManyToOne
    @JoinColumn(name = "exit_guard_id")
    private User exitGuard;

    @Column(name = "exit_time")
    private LocalDateTime exitTime;

    /* ---------- STATUS ---------- */

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovementStatus status;
}
