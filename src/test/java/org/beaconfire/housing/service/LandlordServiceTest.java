package org.beaconfire.housing.service;

import org.beaconfire.housing.entity.Landlord;
import org.beaconfire.housing.repo.LandlordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LandlordServiceTest {

    @Mock
    private LandlordRepository landlordRepository;

    @InjectMocks
    private LandlordService landlordService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAllLandlords() {
        Pageable pageable = PageRequest.of(0, 10);
        Landlord landlord1 = new Landlord();
        landlord1.setId(1);
        landlord1.setFirstName("Alice");

        Landlord landlord2 = new Landlord();
        landlord2.setId(2);
        landlord2.setFirstName("Bob");

        List<Landlord> landlords = Arrays.asList(landlord1, landlord2);
        Page<Landlord> landlordPage = new PageImpl<>(landlords);

        when(landlordRepository.findByFilters(null, null, null, null, pageable)).thenReturn(landlordPage);

        Page<Landlord> result = landlordService.getAllLandlords(null, null, null, null, pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals("Alice", result.getContent().get(0).getFirstName());
        verify(landlordRepository, times(1)).findByFilters(null, null, null, null, pageable);
    }

    @Test
    public void testGetLandlordById() {
        Landlord landlord = new Landlord();
        landlord.setId(1);
        landlord.setFirstName("Alice");
        landlord.setLastName("Smith");

        when(landlordRepository.findById(1)).thenReturn(Optional.of(landlord));

        Landlord result = landlordService.getLandlordById(1);
        assertNotNull(result);
        assertEquals("Alice", result.getFirstName());
        verify(landlordRepository, times(1)).findById(1);
    }

    @Test
    public void testGetLandlordById_NotFound() {
        when(landlordRepository.findById(999)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> landlordService.getLandlordById(999));
    }

    @Test
    public void testAddLandlord() {
        Landlord landlord = new Landlord();
        landlord.setFirstName("Charlie");
        landlord.setLastName("Smith");

        when(landlordRepository.save(landlord)).thenReturn(landlord);

        Landlord result = landlordService.createLandlord(landlord);

        assertNotNull(result);
        assertEquals("Charlie", result.getFirstName());
        verify(landlordRepository, times(1)).save(landlord);
    }

    @Test
    public void testCreateLandlord_NullInput() {
        assertThrows(IllegalArgumentException.class, () -> landlordService.createLandlord(null));
    }

    @Test
    public void testDeleteLandlordById() {
        int landlordId = 1;

        doNothing().when(landlordRepository).deleteById(landlordId);

        landlordService.deleteLandlord(landlordId);

        verify(landlordRepository, times(1)).deleteById(landlordId);
    }

    @Test
    public void testDeleteLandlordById_NotFound() {
        int nonExistentId = 999;
        doThrow(new org.springframework.dao.EmptyResultDataAccessException(1)).when(landlordRepository).deleteById(nonExistentId);
        assertThrows(org.springframework.dao.EmptyResultDataAccessException.class, () -> landlordService.deleteLandlord(nonExistentId));
    }
}
