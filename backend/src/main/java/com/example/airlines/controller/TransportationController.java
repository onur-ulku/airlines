package com.example.airlines.controller;

import com.example.airlines.model.Transportation;
import com.example.airlines.service.TransportationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transportations")
public class TransportationController {

    private final TransportationService transportationService;

    public TransportationController(TransportationService transportationService) {
        this.transportationService = transportationService;
    }

    @GetMapping
    public List<Transportation> getAll() {
        return transportationService.findAll();
    }

    @GetMapping("/{id}")
    public Transportation getById(@PathVariable Long id) {
        return transportationService.findById(id);
    }

    @PostMapping
    public ResponseEntity<Transportation> create(@RequestBody Transportation transportation) {
        Transportation created = transportationService.create(transportation);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public Transportation update(@PathVariable Long id, @RequestBody Transportation transportation) {
        return transportationService.update(id, transportation);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        transportationService.delete(id);
    }
}

