package com.example.airlines.service;

import com.example.airlines.model.Location;
import com.example.airlines.model.Transportation;
import com.example.airlines.model.TransportationType;
import com.example.airlines.repository.LocationRepository;
import com.example.airlines.repository.TransportationRepository;
import com.example.airlines.service.impl.RouteServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RouteServiceTest {

    @Mock
    LocationRepository locationRepository;
    @Mock
    TransportationRepository transportationRepository;
    @InjectMocks
    RouteServiceImpl routeService;

    @Test
    void findRoutes_returnsRouteWhenFlightInMiddle() {
        Location a = location(1L);
        Location b = location(2L);
        Location c = location(3L);
        when(locationRepository.findById(1L)).thenReturn(Optional.of(a));
        when(locationRepository.findById(3L)).thenReturn(Optional.of(c));

        Transportation t1 = transport(100L, a, b, TransportationType.UBER);
        Transportation t2 = transport(200L, b, c, TransportationType.FLIGHT);
        when(transportationRepository.findAll()).thenReturn(List.of(t1, t2));

        var routes = routeService.findRoutes(1L, 3L, LocalDate.of(2025, 3, 10)); // Monday

        assertThat(routes).hasSize(1);
        assertThat(routes.get(0).segments()).hasSize(2);
        assertThat(routes.get(0).segments().get(0).type()).isEqualTo(TransportationType.UBER);
        assertThat(routes.get(0).segments().get(1).type()).isEqualTo(TransportationType.FLIGHT);
    }

    @Test
    void findRoutes_returnsEmptyWhenNoFlight() {
        Location a = location(1L);
        Location b = location(2L);
        Location c = location(3L);
        when(locationRepository.findById(1L)).thenReturn(Optional.of(a));
        when(locationRepository.findById(3L)).thenReturn(Optional.of(c));

        Transportation t1 = transport(10L, a, b, TransportationType.BUS);
        Transportation t2 = transport(20L, b, c, TransportationType.UBER);
        when(transportationRepository.findAll()).thenReturn(List.of(t1, t2));

        var routes = routeService.findRoutes(1L, 3L, LocalDate.of(2025, 3, 10));

        assertThat(routes).isEmpty();
    }

    private static Location location(long id) {
        Location l = new Location();
        l.setId(id);
        return l;
    }

    private static Transportation transport(long id, Location from, Location to, TransportationType type) {
        Transportation t = new Transportation();
        t.setId(id);
        t.setOrigin(from);
        t.setDestination(to);
        t.setType(type);
        t.setOperatingDays(List.of(1)); // Monday
        return t;
    }
}
