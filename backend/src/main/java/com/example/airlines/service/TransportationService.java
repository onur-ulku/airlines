package com.example.airlines.service;

import com.example.airlines.model.Transportation;

import java.util.List;

public interface TransportationService {

    List<Transportation> findAll();

    Transportation findById(Long id);

    Transportation create(Transportation transportation);

    Transportation update(Long id, Transportation updated);

    void delete(Long id);
}
