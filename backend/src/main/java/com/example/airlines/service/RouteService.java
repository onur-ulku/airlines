package com.example.airlines.service;

import com.example.airlines.model.TransportationType;

import java.time.LocalDate;
import java.util.List;

public interface RouteService {

    record RouteSegment(Long transportationId,
                        Long originId,
                        Long destinationId,
                        TransportationType type) {
    }

    record Route(List<RouteSegment> segments) {
    }

    List<Route> findRoutes(Long originId, Long destinationId, LocalDate date);
}
