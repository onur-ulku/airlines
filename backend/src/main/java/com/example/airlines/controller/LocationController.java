package com.example.airlines.controller;

import com.example.airlines.model.Location;
import com.example.airlines.service.LocationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    public List<Location> getAll() {
        return locationService.findAll();
    }

    @GetMapping("/{id}")
    public Location getById(@PathVariable Long id) {
        return locationService.findById(id);
    }

    @PostMapping
    public ResponseEntity<Location> create(@RequestBody Location location) {
        Location created = locationService.create(location);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public Location update(@PathVariable Long id, @RequestBody Location location) {
        return locationService.update(id, location);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        locationService.delete(id);
    }
}

