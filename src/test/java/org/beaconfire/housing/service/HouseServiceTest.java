package org.beaconfire.housing.service;

import org.beaconfire.housing.entity.House;
import org.beaconfire.housing.repo.HouseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HouseServiceTest {

    @Mock
    private HouseRepository houseRepository;

    @InjectMocks
    private HouseService houseService;

    private House testHouse;
    private List<House> testHouses;

    @BeforeEach
    void setUp() {
        // Initialize test data
        testHouse = new House();
        testHouse.setId(1);
        testHouse.setLandlordId(100);
        testHouse.setAddress("123 Main St, Springfield, IL 62701");
        testHouse.setMaxOccupant(4);
        testHouse.setDescription("Beautiful 3-bedroom house with modern amenities");
        testHouse.setCreateDate(Timestamp.from(Instant.now()));
        testHouse.setLastModificationDate(Timestamp.from(Instant.now()));

        House house2 = new House();
        house2.setId(2);
        house2.setLandlordId(101);
        house2.setAddress("456 Oak Ave, Springfield, IL 62702");
        house2.setMaxOccupant(6);
        house2.setDescription("Spacious family home with large backyard");
        house2.setCreateDate(Timestamp.from(Instant.now()));
        house2.setLastModificationDate(Timestamp.from(Instant.now()));

        testHouses = Arrays.asList(testHouse, house2);
    }

    @Test
    void createHouse_ShouldSaveHouseSuccessfully() {
        // Arrange
        when(houseRepository.save(any(House.class))).thenReturn(testHouse);

        // Act
        houseService.createHouse(testHouse);

        // Assert
        verify(houseRepository, times(1)).save(testHouse);
        verify(houseRepository).save(argThat(house ->
                house.getLandlordId().equals(100) &&
                        house.getAddress().equals("123 Main St, Springfield, IL 62701") &&
                        house.getMaxOccupant().equals(4)
        ));
    }

    @Test
    void createHouse_WithMinimalData_ShouldSave() {
        // Arrange
        House minimalHouse = new House();
        minimalHouse.setLandlordId(100); // Only required field

        when(houseRepository.save(any(House.class))).thenReturn(minimalHouse);

        // Act
        houseService.createHouse(minimalHouse);

        // Assert
        verify(houseRepository, times(1)).save(minimalHouse);
    }

//    @Test
//    void getAllHouses_ShouldReturnAllHouses() {
//        // Arrange
//        when(houseRepository.findAll()).thenReturn(testHouses);
//
//        // Act
//        List<House> result = houseService.getAllHouses();
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(2, result.size());
//        assertEquals(testHouse.getId(), result.get(0).getId());
//        assertEquals(100, result.get(0).getLandlordId());
//        assertEquals("456 Oak Ave, Springfield, IL 62702", result.get(1).getAddress());
//        assertEquals(6, result.get(1).getMaxOccupant());
//        verify(houseRepository, times(1)).findAll();
//    }

//    @Test
//    void getAllHouses_ShouldReturnEmptyListWhenNoHouses() {
//        // Arrange
//        when(houseRepository.findAll()).thenReturn(Collections.emptyList());
//
//        // Act
//        List<House> result = houseService.getAllHouses();
//
//        // Assert
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//        verify(houseRepository, times(1)).findAll();
//    }

    @Test
    void createHouse_ShouldHandleRepositoryException() {
        // Arrange
        when(houseRepository.save(any(House.class)))
                .thenThrow(new RuntimeException("Database connection error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            houseService.createHouse(testHouse);
        });

        assertEquals("Database connection error", exception.getMessage());
        verify(houseRepository, times(1)).save(testHouse);
    }
}