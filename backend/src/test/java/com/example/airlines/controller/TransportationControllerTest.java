package com.example.airlines.controller;

import com.example.airlines.model.Location;
import com.example.airlines.model.Transportation;
import com.example.airlines.model.TransportationType;
import com.example.airlines.service.TransportationService;
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
class TransportationControllerTest {

    @Mock
    TransportationService transportationService;
    @InjectMocks
    TransportationController controller;

    @Test
    void getAll_returnsServiceList() {
        Transportation t = new Transportation();
        t.setId(1L);
        t.setType(TransportationType.FLIGHT);
        when(transportationService.findAll()).thenReturn(List.of(t));

        List<Transportation> result = controller.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        verify(transportationService).findAll();
    }

    @Test
    void getById_returnsServiceResult() {
        Transportation t = new Transportation();
        t.setId(1L);
        t.setType(TransportationType.FLIGHT);
        when(transportationService.findById(1L)).thenReturn(t);

        Transportation result = controller.getById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getType()).isEqualTo(TransportationType.FLIGHT);
        verify(transportationService).findById(1L);
    }

    @Test
    void create_callsServiceAndReturnsCreated() {
        Transportation input = new Transportation();
        input.setType(TransportationType.FLIGHT);
        Location o = new Location();
        o.setId(1L);
        Location d = new Location();
        d.setId(2L);
        input.setOrigin(o);
        input.setDestination(d);
        Transportation created = new Transportation();
        created.setId(10L);
        when(transportationService.create(any(Transportation.class))).thenReturn(created);

        ResponseEntity<Transportation> result = controller.create(input);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getId()).isEqualTo(10L);
        verify(transportationService).create(input);
    }

    @Test
    void update_callsServiceAndReturnsResult() {
        Transportation input = new Transportation();
        input.setType(TransportationType.BUS);
        Transportation updated = new Transportation();
        updated.setId(1L);
        updated.setType(TransportationType.BUS);
        when(transportationService.update(eq(1L), any(Transportation.class))).thenReturn(updated);

        Transportation result = controller.update(1L, input);

        assertThat(result.getType()).isEqualTo(TransportationType.BUS);
        verify(transportationService).update(1L, input);
    }

    @Test
    void delete_callsService() {
        controller.delete(1L);
        verify(transportationService).delete(1L);
    }
}
