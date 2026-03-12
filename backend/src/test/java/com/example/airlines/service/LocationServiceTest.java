package com.example.airlines.service;

import com.example.airlines.model.Location;
import com.example.airlines.repository.LocationRepository;
import com.example.airlines.repository.TransportationRepository;
import com.example.airlines.service.impl.LocationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

    @Mock
    LocationRepository locationRepository;
    @Mock
    TransportationRepository transportationRepository;
    @InjectMocks
    LocationServiceImpl locationService;

    @Test
    void findAll_delegatesToRepository() {
        Location l = new Location();
        l.setId(1L);
        when(locationRepository.findAll()).thenReturn(List.of(l));

        assertThat(locationService.findAll()).hasSize(1).first().extracting(Location::getId).isEqualTo(1L);
        verify(locationRepository).findAll();
    }

    @Test
    void findById_returnsFromRepository() {
        Location l = new Location();
        l.setId(1L);
        when(locationRepository.findById(1L)).thenReturn(Optional.of(l));

        assertThat(locationService.findById(1L).getId()).isEqualTo(1L);
        verify(locationRepository).findById(1L);
    }

    @Test
    void create_savesAndReturns() {
        Location input = new Location();
        input.setName("IST");
        Location saved = new Location();
        saved.setId(10L);
        saved.setName("IST");
        when(locationRepository.save(any(Location.class))).thenReturn(saved);

        Location result = locationService.create(input);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getName()).isEqualTo("IST");
        verify(locationRepository).save(input);
    }

    @Test
    void update_loadsSavesAndReturns() {
        Location existing = new Location();
        existing.setId(1L);
        Location updated = new Location();
        updated.setName("New");
        when(locationRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(locationRepository.save(any(Location.class))).thenReturn(existing);

        Location result = locationService.update(1L, updated);

        assertThat(result.getName()).isEqualTo("New");
        verify(locationRepository).save(existing);
    }

    @Test
    void delete_whenNotInUse_deletes() {
        when(transportationRepository.existsByOriginIdOrDestinationId(5L)).thenReturn(false);

        locationService.delete(5L);

        verify(locationRepository).deleteById(5L);
    }

    @Test
    void delete_whenInUse_throws() {
        when(transportationRepository.existsByOriginIdOrDestinationId(5L)).thenReturn(true);

        assertThatThrownBy(() -> locationService.delete(5L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Location is used by");
        verify(locationRepository, never()).deleteById(anyLong());
    }
}
