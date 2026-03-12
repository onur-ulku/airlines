package com.example.airlines.service;

import com.example.airlines.model.Location;
import com.example.airlines.model.Transportation;
import com.example.airlines.model.TransportationType;
import com.example.airlines.repository.LocationRepository;
import com.example.airlines.repository.TransportationRepository;
import com.example.airlines.service.impl.TransportationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransportationServiceTest {

    @Mock
    TransportationRepository transportationRepository;
    @Mock
    LocationRepository locationRepository;
    @Mock
    ApplicationEventPublisher eventPublisher;
    @InjectMocks
    TransportationServiceImpl transportationService;

    @Test
    void findAll_delegatesToRepository() {
        Transportation t = new Transportation();
        t.setId(1L);
        when(transportationRepository.findAll()).thenReturn(List.of(t));

        assertThat(transportationService.findAll()).hasSize(1);
        verify(transportationRepository).findAll();
    }

    @Test
    void findById_returnsFromRepository() {
        Transportation t = new Transportation();
        t.setId(1L);
        t.setType(TransportationType.FLIGHT);
        when(transportationRepository.findById(1L)).thenReturn(Optional.of(t));

        assertThat(transportationService.findById(1L).getType()).isEqualTo(TransportationType.FLIGHT);
        verify(transportationRepository).findById(1L);
    }

    @Test
    void create_resolvesLocationsAndSaves() {
        Location origin = new Location();
        origin.setId(1L);
        Location dest = new Location();
        dest.setId(2L);
        when(locationRepository.findById(1L)).thenReturn(Optional.of(origin));
        when(locationRepository.findById(2L)).thenReturn(Optional.of(dest));
        when(transportationRepository.save(any(Transportation.class))).thenAnswer(i -> i.getArgument(0));

        Transportation input = new Transportation();
        input.setOrigin(origin);
        input.setDestination(dest);
        input.setType(TransportationType.FLIGHT);

        Transportation result = transportationService.create(input);

        assertThat(result.getType()).isEqualTo(TransportationType.FLIGHT);
        verify(transportationRepository).save(any(Transportation.class));
    }

    @Test
    void update_resolvesAndSaves() {
        Transportation existing = new Transportation();
        existing.setId(10L);
        Location origin = new Location();
        origin.setId(1L);
        Location dest = new Location();
        dest.setId(2L);
        when(transportationRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(locationRepository.findById(1L)).thenReturn(Optional.of(origin));
        when(locationRepository.findById(2L)).thenReturn(Optional.of(dest));
        when(transportationRepository.save(any(Transportation.class))).thenAnswer(i -> i.getArgument(0));

        Transportation input = new Transportation();
        input.setOrigin(origin);
        input.setDestination(dest);
        input.setType(TransportationType.BUS);
        input.setOperatingDays(List.of(1, 3, 5));

        Transportation result = transportationService.update(10L, input);

        assertThat(result.getType()).isEqualTo(TransportationType.BUS);
        assertThat(result.getOperatingDays()).containsExactly(1, 3, 5);
        verify(transportationRepository).save(existing);
    }

    @Test
    void delete_delegatesToRepository() {
        transportationService.delete(7L);
        verify(transportationRepository).deleteById(7L);
    }
}
