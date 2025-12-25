package com.example.safecamp.entity;

import java.time.LocalDateTime;

import com.example.safecamp.entity.base.BaseEntity;
import com.example.safecamp.enums.GuestVisitStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "guest_visits")
public class GuestVisit extends BaseEntity {
    @ManyToOne(optional = false)
    private User guest;

    @ManyToOne(optional = false)
    private User hostResident;

    @ManyToOne(optional = false)
    private Gate expectedGate;


    @Column(nullable = false)
    private LocalDateTime expectedEntryTime;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GuestVisitStatus status;

    @OneToOne
    private User checkedBy;

    @OneToOne
    private EntryExitLog entryExitLog;
}
