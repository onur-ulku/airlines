package com.example.airlines.service;

import com.example.airlines.model.Location;

import java.util.List;

public interface LocationService {

    List<Location> findAll();

    Location findById(Long id);

    Location create(Location location);

    Location update(Long id, Location updated);

    void delete(Long id);
}
