package com.example.airlines.repository;

import com.example.airlines.model.Location;
import com.example.airlines.model.Transportation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransportationRepository extends JpaRepository<Transportation, Long> {

    List<Transportation> findByOrigin(Location origin);

    List<Transportation> findByDestination(Location destination);

    @Query("SELECT COUNT(t) > 0 FROM Transportation t WHERE t.origin.id = :locationId OR t.destination.id = :locationId")
    boolean existsByOriginIdOrDestinationId(@Param("locationId") Long locationId);
}

