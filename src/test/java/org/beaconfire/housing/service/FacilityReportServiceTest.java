package org.beaconfire.housing.service;

import org.beaconfire.housing.dto.request.CreateReportRequestDTO;
import org.beaconfire.housing.dto.response.ReportDTO;
import org.beaconfire.housing.entity.Facility;
import org.beaconfire.housing.entity.FacilityReport;
import org.beaconfire.housing.entity.House;
import org.beaconfire.housing.exception.FacilityNotFoundException;
import org.beaconfire.housing.exception.ReportNotFoundException;
import org.beaconfire.housing.repo.FacilityReportRepository;
import org.beaconfire.housing.repo.FacilityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FacilityReportServiceTest {

    @Mock
    private FacilityRepository facilityRepository;

    @Mock
    private FacilityReportRepository facilityReportRepository;

    @InjectMocks
    private FacilityReportService facilityReportService;

    private Facility testFacility;
    private FacilityReport testReport;
    private CreateReportRequestDTO createReportRequest;
    private House testHouse;

    @BeforeEach
    void setUp() {
        // Setup test house
        testHouse = new House();
        testHouse.setId(1);

        // Setup test facility
        testFacility = new Facility();
        testFacility.setId(1);
        testFacility.setType("Bed");
        testFacility.setHouse(testHouse);

        // Setup test report
        testReport = FacilityReport.builder()
                .id(1)
                .facility(testFacility)
                .employeeId("123")
                .title("Broken Bed Frame")
                .description("Bed frame is broken and needs replacement")
                .status("Open")
                .createDate(new Timestamp(System.currentTimeMillis()))
                .build();

        // Setup create report request
        createReportRequest = new CreateReportRequestDTO();
        createReportRequest.setFacilityName("Bed");
        createReportRequest.setTitle("Broken Bed Frame");
        createReportRequest.setDescription("Bed frame is broken and needs replacement");
    }

    @Test
    void createFacilityReport_Success() {
        // Given
        Integer houseId = 1;
        String employeeId = "123";  // Changed to String

        when(facilityRepository.findByHouseIdAndType(houseId, "Bed"))
                .thenReturn(Optional.of(testFacility));

        // Mock the save to return a complete report with ID
        when(facilityReportRepository.save(any(FacilityReport.class)))
                .thenAnswer(invocation -> {
                    FacilityReport savedReport = invocation.getArgument(0);
                    // Simulate database save by setting ID and timestamp
                    savedReport.setId(1);
                    savedReport.setCreateDate(new Timestamp(System.currentTimeMillis()));
                    return savedReport;
                });

        // When
        ReportDTO result = facilityReportService.createFacilityReport(
                houseId, createReportRequest, employeeId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Bed", result.getFacilityName());
        assertEquals("Broken Bed Frame", result.getTitle());
        assertEquals("Bed frame is broken and needs replacement", result.getDescription());
        assertEquals("Open", result.getStatus());
        assertEquals("123", result.getCreatedBy());

        // Verify interactions
        verify(facilityRepository, times(1)).findByHouseIdAndType(houseId, "Bed");
        verify(facilityReportRepository, times(1)).save(any(FacilityReport.class));
    }

    @Test
    void createFacilityReport_FacilityNotFound_ThrowsException() {
        // Given
        Integer houseId = 1;
        String employeeId = "123";  // Changed to String

        when(facilityRepository.findByHouseIdAndType(houseId, "Bed"))
                .thenReturn(Optional.empty());

        // When & Then
        FacilityNotFoundException exception = assertThrows(
                FacilityNotFoundException.class,
                () -> facilityReportService.createFacilityReport(houseId, createReportRequest, employeeId)
        );

        assertEquals("Facility Bed not found in this house", exception.getMessage());

        // Verify repository was called but save was not
        verify(facilityRepository, times(1)).findByHouseIdAndType(houseId, "Bed");
        verify(facilityReportRepository, never()).save(any(FacilityReport.class));
    }

    @Test
    void getFacilityReportById_Success() {
        // Given
        Integer reportId = 1;
        when(facilityReportRepository.findById(reportId))
                .thenReturn(Optional.of(testReport));

        // When
        ReportDTO result = facilityReportService.getFacilityReportById(reportId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Bed", result.getFacilityName());
        assertEquals("Broken Bed Frame", result.getTitle());
        assertEquals("Bed frame is broken and needs replacement", result.getDescription());
        assertEquals("Open", result.getStatus());
        assertEquals("123", result.getCreatedBy());  // Already expecting String

        verify(facilityReportRepository, times(1)).findById(reportId);
    }

    @Test
    void getFacilityReportById_NotFound_ThrowsException() {
        // Given
        Integer reportId = 999;
        when(facilityReportRepository.findById(reportId))
                .thenReturn(Optional.empty());

        // When & Then
        ReportNotFoundException exception = assertThrows(
                ReportNotFoundException.class,
                () -> facilityReportService.getFacilityReportById(reportId)
        );

        assertEquals("Report not found", exception.getMessage());
        verify(facilityReportRepository, times(1)).findById(reportId);
    }

    @Test
    void createFacilityReport_VerifySavedReportFields() {
        // Given
        Integer houseId = 1;
        String employeeId = "123";  // Changed to String

        when(facilityRepository.findByHouseIdAndType(houseId, "Bed"))
                .thenReturn(Optional.of(testFacility));
        when(facilityReportRepository.save(any(FacilityReport.class)))
                .thenAnswer(invocation -> {
                    FacilityReport savedReport = invocation.getArgument(0);
                    savedReport.setId(1);
                    savedReport.setCreateDate(new Timestamp(System.currentTimeMillis()));
                    return savedReport;
                });

        // When
        ReportDTO result = facilityReportService.createFacilityReport(
                houseId, createReportRequest, employeeId);

        // Then
        verify(facilityReportRepository).save(argThat(report ->
                report.getFacility().equals(testFacility) &&
                        report.getEmployeeId().equals(employeeId) &&
                        report.getTitle().equals("Broken Bed Frame") &&
                        report.getDescription().equals("Bed frame is broken and needs replacement") &&
                        report.getStatus().equals("Open")
        ));
    }

    @Test
    void createFacilityReport_WithDifferentFacilityTypes() {
        // Test with different facility types
        String[] facilityTypes = {"Air Conditioner", "Kitchen", "Bathroom", "Window"};

        for (String facilityType : facilityTypes) {
            // Setup
            House house = new House();
            house.setId(1);

            Facility facility = new Facility();
            facility.setId(100);
            facility.setType(facilityType);
            facility.setHouse(house);

            createReportRequest.setFacilityName(facilityType);

            when(facilityRepository.findByHouseIdAndType(1, facilityType))
                    .thenReturn(Optional.of(facility));
            when(facilityReportRepository.save(any(FacilityReport.class)))
                    .thenAnswer(invocation -> {
                        FacilityReport report = invocation.getArgument(0);
                        report.setId(100);
                        report.setCreateDate(new Timestamp(System.currentTimeMillis()));
                        return report;
                    });

            // Execute
            ReportDTO result = facilityReportService.createFacilityReport(
                    1, createReportRequest, "123");  // Changed to String

            // Verify
            assertEquals(facilityType, result.getFacilityName());
        }
    }

    @Test
    void createFacilityReport_WithMongoDBObjectId() {
        // Given - using MongoDB ObjectId format
        Integer houseId = 1;
        String employeeId = "507f1f77bcf86cd799439011";  // MongoDB ObjectId format

        when(facilityRepository.findByHouseIdAndType(houseId, "Bed"))
                .thenReturn(Optional.of(testFacility));
        when(facilityReportRepository.save(any(FacilityReport.class)))
                .thenAnswer(invocation -> {
                    FacilityReport savedReport = invocation.getArgument(0);
                    savedReport.setId(1);
                    savedReport.setCreateDate(new Timestamp(System.currentTimeMillis()));
                    return savedReport;
                });

        // When
        ReportDTO result = facilityReportService.createFacilityReport(
                houseId, createReportRequest, employeeId);

        // Then
        assertNotNull(result);
        assertEquals("507f1f77bcf86cd799439011", result.getCreatedBy());

        verify(facilityReportRepository).save(argThat(report ->
                report.getEmployeeId().equals("507f1f77bcf86cd799439011")
        ));
    }
}