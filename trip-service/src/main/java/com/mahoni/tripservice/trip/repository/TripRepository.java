package com.mahoni.tripservice.trip.repository;

import com.mahoni.tripservice.trip.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TripRepository extends JpaRepository<Trip, UUID> {

  Optional<List<Trip>> findByUserId(UUID userId);

  Optional<List<Trip>> findByStatus(String status);

  @Query("SELECT a FROM Trip a WHERE a.userId = ?1 ORDER BY a.scanInAt DESC LIMIT 1")
  Optional<Trip> findLatestTripByUserId(UUID userId);
}
