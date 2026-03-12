package com.example.airlines.service.impl;

import com.example.airlines.model.Location;
import com.example.airlines.model.Transportation;
import com.example.airlines.model.TransportationType;
import com.example.airlines.repository.LocationRepository;
import com.example.airlines.repository.TransportationRepository;
import com.example.airlines.service.RouteService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RouteServiceImpl implements RouteService {

    private final LocationRepository locationRepository;
    private final TransportationRepository transportationRepository;

    public RouteServiceImpl(LocationRepository locationRepository,
                            TransportationRepository transportationRepository) {
        this.locationRepository = locationRepository;
        this.transportationRepository = transportationRepository;
    }

    @Override
    @Cacheable("routes")
    public List<Route> findRoutes(Long originId, Long destinationId, LocalDate date) {
        Location origin = locationRepository.findById(originId).orElseThrow();
        Location destination = locationRepository.findById(destinationId).orElseThrow();

        int dayOfWeek = mapDayOfWeek(date.getDayOfWeek());
        List<Transportation> allAvailable = transportationRepository.findAll()
                .stream()
                .filter(t -> t.getOperatingDays() != null && t.getOperatingDays().contains(dayOfWeek))
                .collect(Collectors.toList());

        List<Route> result = new ArrayList<>();
        List<Transportation> path = new ArrayList<>();
        dfs(origin, destination, allAvailable, path, result, 0);
        return result;
    }

    private void dfs(Location current,
                     Location target,
                     List<Transportation> all,
                     List<Transportation> path,
                     List<Route> result,
                     int depth) {
        if (depth > 3) {
            return;
        }

        if (!path.isEmpty() && current.getId().equals(target.getId())) {
            if (isValidRoute(path)) {
                result.add(toRoute(path));
            }
            if (depth == 3) {
                return;
            }
        }

        for (Transportation t : all) {
            if (t.getOrigin().getId().equals(current.getId())) {
                path.add(t);
                dfs(t.getDestination(), target, all, path, result, depth + 1);
                path.remove(path.size() - 1);
            }
        }
    }

    private boolean isValidRoute(List<Transportation> transports) {
        if (transports.size() == 0 || transports.size() > 3) {
            return false;
        }

        long flights = transports.stream().filter(t -> t.getType() == TransportationType.FLIGHT).count();
        long before = 0;
        long after = 0;

        int flightIndex = -1;
        for (int i = 0; i < transports.size(); i++) {
            if (transports.get(i).getType() == TransportationType.FLIGHT) {
                flightIndex = i;
                break;
            }
        }

        if (flightIndex == -1) {
            return false;
        }

        for (int i = 0; i < transports.size(); i++) {
            Transportation t = transports.get(i);
            if (i < flightIndex && t.getType() != TransportationType.FLIGHT) {
                before++;
            } else if (i > flightIndex && t.getType() != TransportationType.FLIGHT) {
                after++;
            }
        }

        if (flights != 1) {
            return false;
        }
        if (before > 1 || after > 1) {
            return false;
        }

        return true;
    }

    private Route toRoute(List<Transportation> transports) {
        List<RouteSegment> segments = transports.stream()
                .map(t -> new RouteSegment(
                        t.getId(),
                        t.getOrigin().getId(),
                        t.getDestination().getId(),
                        t.getType()
                ))
                .toList();
        return new Route(segments);
    }

    private int mapDayOfWeek(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> 1;
            case TUESDAY -> 2;
            case WEDNESDAY -> 3;
            case THURSDAY -> 4;
            case FRIDAY -> 5;
            case SATURDAY -> 6;
            case SUNDAY -> 7;
        };
    }
}
