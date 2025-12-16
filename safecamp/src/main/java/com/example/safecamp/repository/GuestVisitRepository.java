package com.example.safecamp.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.safecamp.entity.GuestVisit;
import com.example.safecamp.entity.User;
import com.example.safecamp.enums.GuestVisitStatus;

public interface GuestVisitRepository extends JpaRepository<GuestVisit, UUID> {
    Optional<GuestVisit> findByGuestAndStatus(
            User guest,
            GuestVisitStatus status);

    boolean existsByGuestAndStatusIn(User guest, List<GuestVisitStatus> of);

}
