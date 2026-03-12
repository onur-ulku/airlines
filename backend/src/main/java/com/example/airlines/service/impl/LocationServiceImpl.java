package com.example.airlines.service.impl;

import com.example.airlines.model.Location;
import com.example.airlines.repository.LocationRepository;
import com.example.airlines.repository.TransportationRepository;
import com.example.airlines.service.LocationService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final TransportationRepository transportationRepository;

    public LocationServiceImpl(LocationRepository locationRepository,
                               TransportationRepository transportationRepository) {
        this.locationRepository = locationRepository;
        this.transportationRepository = transportationRepository;
    }

    @Override
    public List<Location> findAll() {
        return locationRepository.findAll();
    }

    @Override
    public Location findById(Long id) {
        return locationRepository.findById(id).orElseThrow();
    }

    @Override
    public Location create(Location location) {
        return locationRepository.save(location);
    }

    @Override
    public Location update(Long id, Location updated) {
        Location existing = findById(id);
        existing.setName(updated.getName());
        existing.setCountry(updated.getCountry());
        existing.setCity(updated.getCity());
        existing.setCode(updated.getCode());
        return locationRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (transportationRepository.existsByOriginIdOrDestinationId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Location is used by one or more transportations and cannot be deleted.");
        }
        locationRepository.deleteById(id);
    }
}
