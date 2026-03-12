package com.example.airlines.controller;

import com.example.airlines.model.Location;
import com.example.airlines.service.LocationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocationControllerTest {

    @Mock
    LocationService locationService;
    @InjectMocks
    LocationController controller;

    @Test
    void getAll_returnsServiceList() {
        Location l = new Location();
        l.setId(1L);
        l.setName("IST");
        when(locationService.findAll()).thenReturn(List.of(l));

        List<Location> result = controller.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getName()).isEqualTo("IST");
        verify(locationService).findAll();
    }

    @Test
    void getById_returnsServiceResult() {
        Location l = new Location();
        l.setId(1L);
        l.setName("IST");
        when(locationService.findById(1L)).thenReturn(l);

        Location result = controller.getById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("IST");
        verify(locationService).findById(1L);
    }

    @Test
    void create_callsServiceAndReturnsCreated() {
        Location input = new Location();
        input.setName("IST");
        Location created = new Location();
        created.setId(10L);
        created.setName("IST");
        when(locationService.create(any(Location.class))).thenReturn(created);

        ResponseEntity<Location> result = controller.create(input);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getId()).isEqualTo(10L);
        verify(locationService).create(input);
    }

    @Test
    void update_callsServiceAndReturnsResult() {
        Location input = new Location();
        input.setName("New");
        Location updated = new Location();
        updated.setId(1L);
        updated.setName("New");
        when(locationService.update(eq(1L), any(Location.class))).thenReturn(updated);

        Location result = controller.update(1L, input);

        assertThat(result.getName()).isEqualTo("New");
        verify(locationService).update(1L, input);
    }

    @Test
    void delete_callsService() {
        controller.delete(1L);
        verify(locationService).delete(1L);
    }
}
