package com.example.airlines.service.impl;

import com.example.airlines.model.Location;
import com.example.airlines.model.Transportation;
import com.example.airlines.repository.LocationRepository;
import com.example.airlines.repository.TransportationRepository;
import com.example.airlines.event.TransportationsChangedEvent;
import com.example.airlines.service.TransportationService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransportationServiceImpl implements TransportationService {

    private final TransportationRepository transportationRepository;
    private final LocationRepository locationRepository;
    private final ApplicationEventPublisher eventPublisher;

    public TransportationServiceImpl(TransportationRepository transportationRepository,
                                    LocationRepository locationRepository,
                                    ApplicationEventPublisher eventPublisher) {
        this.transportationRepository = transportationRepository;
        this.locationRepository = locationRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public List<Transportation> findAll() {
        return transportationRepository.findAll();
    }

    @Override
    public Transportation findById(Long id) {
        return transportationRepository.findById(id).orElseThrow();
    }

    @Override
    @Transactional
    public Transportation create(Transportation transportation) {
        resolveLocations(transportation);
        Transportation saved = transportationRepository.save(transportation);
        eventPublisher.publishEvent(new TransportationsChangedEvent(this));
        return saved;
    }

    @Override
    @Transactional
    public Transportation update(Long id, Transportation updated) {
        Transportation existing = findById(id);
        resolveLocations(updated);
        existing.setOrigin(updated.getOrigin());
        existing.setDestination(updated.getDestination());
        existing.setType(updated.getType());
        List<Integer> days = updated.getOperatingDays();
        existing.setOperatingDays(days != null ? new ArrayList<>(days) : new ArrayList<>());
        Transportation saved = transportationRepository.save(existing);
        eventPublisher.publishEvent(new TransportationsChangedEvent(this));
        return saved;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        transportationRepository.deleteById(id);
        eventPublisher.publishEvent(new TransportationsChangedEvent(this));
    }

    private void resolveLocations(Transportation transportation) {
        if (transportation.getOrigin() != null && transportation.getOrigin().getId() != null) {
            Location origin = locationRepository.findById(transportation.getOrigin().getId()).orElseThrow();
            transportation.setOrigin(origin);
        }
        if (transportation.getDestination() != null && transportation.getDestination().getId() != null) {
            Location destination = locationRepository.findById(transportation.getDestination().getId()).orElseThrow();
            transportation.setDestination(destination);
        }
    }
}
