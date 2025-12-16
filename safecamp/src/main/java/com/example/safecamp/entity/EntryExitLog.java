package com.example.safecamp.entity;

import java.time.LocalDateTime;

import com.example.safecamp.entity.base.BaseEntity;
import com.example.safecamp.enums.MovementStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name="entry_exit_logs")
public class EntryExitLog extends BaseEntity{
    @ManyToOne(optional=false)
    private User user;

    @ManyToOne(optional=false)
    private Gate gate;

    @Column(nullable=false)
    private LocalDateTime entryTime;

    @Column
    private LocalDateTime exitTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private MovementStatus status;
}
