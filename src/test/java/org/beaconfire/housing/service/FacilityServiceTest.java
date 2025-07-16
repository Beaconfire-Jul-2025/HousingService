package org.beaconfire.housing.service;

import org.beaconfire.housing.entity.Facility;
import org.beaconfire.housing.entity.House;
import org.beaconfire.housing.repo.FacilityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FacilityServiceTest {

    @Mock
    private FacilityRepository facilityRepository;

    @InjectMocks
    private FacilityService facilityService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetFacilities() {
        Pageable pageable = PageRequest.of(0, 10);
        Facility f1 = new Facility(); f1.setId(1); f1.setType("AC");
        Facility f2 = new Facility(); f2.setId(2); f2.setType("Heater");
        Page<Facility> page = new PageImpl<>(Arrays.asList(f1, f2));

        when(facilityRepository.findByFilters(null, null, null, pageable)).thenReturn(page);

        Page<Facility> result = facilityService.getFacilities(null, null, null, pageable);

        assertEquals(2, result.getTotalElements());
        verify(facilityRepository).findByFilters(null, null, null, pageable);
    }

    @Test
    public void testGetFacilityById() {
        Facility facility = new Facility();
        facility.setId(1);
        when(facilityRepository.findById(1)).thenReturn(Optional.of(facility));

        Facility result = facilityService.getFacilityById(1);
        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    public void testGetFacilityById_NotFound() {
        when(facilityRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> facilityService.getFacilityById(999));
    }

    @Test
    public void testCreateFacility() {
        Facility facility = new Facility();
        facility.setType("AC");
        when(facilityRepository.save(facility)).thenReturn(facility);

        Facility result = facilityService.createFacility(facility);
        assertNotNull(result);
        assertEquals("AC", result.getType());
    }

    @Test
    public void testUpdateFacility() {
        Facility existing = new Facility();
        existing.setId(1);
        Facility updated = new Facility();
        updated.setType("AC");
        updated.setQuantity(2);
        updated.setDescription("Air Conditioner");
        updated.setHouse(new House());

        when(facilityRepository.findById(1)).thenReturn(Optional.of(existing));
        when(facilityRepository.save(any(Facility.class))).thenReturn(updated);

        Facility result = facilityService.updateFacility(1, updated);
        assertEquals("AC", result.getType());
    }

    @Test
    public void testUpdateFacility_NotFound() {
        when(facilityRepository.findById(999)).thenReturn(Optional.empty());
        Facility dummy = new Facility();

        assertThrows(IllegalArgumentException.class, () -> facilityService.updateFacility(999, dummy));
    }

    @Test
    public void testDeleteFacility() {
        Facility facility = new Facility();
        facility.setId(1);
        when(facilityRepository.findById(1)).thenReturn(Optional.of(facility));
        doNothing().when(facilityRepository).delete(facility);

        facilityService.deleteFacility(1);
        verify(facilityRepository).delete(facility);
    }

    @Test
    public void testDeleteFacility_NotFound() {
        when(facilityRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> facilityService.deleteFacility(999));
    }
}
