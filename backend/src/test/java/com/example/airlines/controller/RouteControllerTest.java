package com.example.airlines.controller;

import com.example.airlines.model.TransportationType;
import com.example.airlines.service.RouteService;
import com.example.airlines.service.RouteService.Route;
import com.example.airlines.service.RouteService.RouteSegment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RouteControllerTest {

    @Mock
    RouteService routeService;
    @InjectMocks
    RouteController controller;

    @Test
    void findRoutes_returnsServiceList() {
        LocalDate date = LocalDate.of(2025, 3, 10);
        Route route = new Route(List.of(
                new RouteSegment(1L, 1L, 2L, TransportationType.FLIGHT)
        ));
        when(routeService.findRoutes(1L, 2L, date)).thenReturn(List.of(route));

        List<Route> result = controller.findRoutes(1L, 2L, date);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).segments()).hasSize(1);
        assertThat(result.get(0).segments().get(0).type()).isEqualTo(TransportationType.FLIGHT);
        verify(routeService).findRoutes(1L, 2L, date);
    }

    @Test
    void findRoutes_emptyList() {
        LocalDate date = LocalDate.of(2025, 3, 10);
        when(routeService.findRoutes(1L, 2L, date)).thenReturn(List.of());

        List<Route> result = controller.findRoutes(1L, 2L, date);

        assertThat(result).isEmpty();
        verify(routeService).findRoutes(1L, 2L, date);
    }
}
