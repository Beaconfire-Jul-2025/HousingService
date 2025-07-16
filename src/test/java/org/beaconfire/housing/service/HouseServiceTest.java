package org.beaconfire.housing.service;

import org.beaconfire.housing.entity.House;
import org.beaconfire.housing.repo.HouseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class HouseServiceTest {

    @InjectMocks
    private HouseService houseService;

    @Mock
    private HouseRepository houseRepository;

    @Test
    void testGetHouseById_Found() {
        House house = new House();
        house.setId(1);
        when(houseRepository.findById(1)).thenReturn(Optional.of(house));
        assertEquals(house, houseService.getHouseById(1));
    }

    @Test
    void testGetHouseById_NotFound() {
        when(houseRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> houseService.getHouseById(1));
    }

    @Test
    void testCreateHouse() {
        House house = new House();
        when(houseRepository.save(house)).thenReturn(house);
        assertEquals(house, houseService.createHouse(house));
    }

    @Test
    void testDeleteHouseById() {
        houseService.deleteHouseById(1);
        verify(houseRepository, times(1)).deleteById(1);
    }

    @Test
    void testUpdateHouse() {
        House house = new House();
        when(houseRepository.save(house)).thenReturn(house);
        assertEquals(house, houseService.updateHouse(house));
    }

    @Test
    void testHouseExists() {
        when(houseRepository.existsById(1)).thenReturn(true);
        assertTrue(houseService.houseExists(1));
    }

    @Test
    void testGetHouses() {
        Pageable pageable = PageRequest.of(0, 10);
        House house = new House();
        Page<House> page = new PageImpl<>(Collections.singletonList(house));

        when(houseRepository.findByFilters(null, null, null, null, pageable)).thenReturn(page);

        Page<House> result = houseService.getHouses(null, null, null, null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(house, result.getContent().get(0));
    }

    @Test
    void testGetCurrentOccupant_Found() {
        House house = new House();
        house.setCurrentOccupant(3);
        when(houseRepository.findById(1)).thenReturn(Optional.of(house));
        assertEquals(3, houseService.getCurrentOccupant(1));
    }

    @Test
    void testGetCurrentOccupant_NotFound() {
        when(houseRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> houseService.getCurrentOccupant(1));
    }

    @Test
    void testIncrementOccupant_Success() {
        House house = new House();
        house.setCurrentOccupant(2);
        house.setMaxOccupant(3);
        when(houseRepository.findById(1)).thenReturn(Optional.of(house));
        houseService.incrementOccupant(1);
        assertEquals(3, house.getCurrentOccupant());
    }

    @Test
    void testIncrementOccupant_ExceedsMax() {
        House house = new House();
        house.setCurrentOccupant(3);
        house.setMaxOccupant(3);
        when(houseRepository.findById(1)).thenReturn(Optional.of(house));
        assertThrows(IllegalStateException.class, () -> houseService.incrementOccupant(1));
    }

    @Test
    void testDecrementOccupant_Success() {
        House house = new House();
        house.setCurrentOccupant(2);
        when(houseRepository.findById(1)).thenReturn(Optional.of(house));
        houseService.decrementOccupant(1);
        assertEquals(1, house.getCurrentOccupant());
    }

    @Test
    void testDecrementOccupant_BelowZero() {
        House house = new House();
        house.setCurrentOccupant(0);
        when(houseRepository.findById(1)).thenReturn(Optional.of(house));
        assertThrows(IllegalStateException.class, () -> houseService.decrementOccupant(1));
    }
}