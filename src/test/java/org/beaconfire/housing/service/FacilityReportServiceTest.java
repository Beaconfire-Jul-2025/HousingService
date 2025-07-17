package org.beaconfire.housing.service;

import org.beaconfire.housing.dto.CommentDTO;
import org.beaconfire.housing.dto.request.CreateReportRequest;
import org.beaconfire.housing.dto.response.ReportDetailResponse;
import org.beaconfire.housing.dto.response.ReportListResponse;
import org.beaconfire.housing.dto.response.ReportResponse;
import org.beaconfire.housing.dto.response.UpdateReportStatusResponse;
import org.beaconfire.housing.entity.Facility;
import org.beaconfire.housing.entity.FacilityReport;
import org.beaconfire.housing.entity.FacilityReportDetail;
import org.beaconfire.housing.entity.House;
import org.beaconfire.housing.exception.FacilityNotFoundException;
import org.beaconfire.housing.exception.ReportNotFoundException;
import org.beaconfire.housing.repo.FacilityReportDetailRepository;
import org.beaconfire.housing.repo.FacilityReportRepository;
import org.beaconfire.housing.repo.FacilityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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

    @Mock
    private FacilityReportDetailRepository facilityReportDetailRepository;

    @InjectMocks
    private FacilityReportService facilityReportService;

    private Facility testFacility;
    private FacilityReport testReport;
    private FacilityReport testReport2;
    private FacilityReportDetail testComment1;
    private FacilityReportDetail testComment2;

    @BeforeEach
    void setUp() {
        // Setup test facility
        House testHouse = new House();
        testHouse.setId(1);

        testFacility = new Facility();
        testFacility.setId(1);
        testFacility.setType("PLUMBING");
        testFacility.setHouse(testHouse);

        // Setup test report
        testReport = FacilityReport.builder()
                .id(1)
                .facility(testFacility)
                .employeeId("emp_123")
                .title("Leaking Faucet")
                .description("Kitchen faucet is leaking")
                .status("OPEN")
                .createDate(Timestamp.valueOf(LocalDateTime.now()))
                .build();

        // Setup second test report
        testReport2 = FacilityReport.builder()
                .id(2)
                .facility(testFacility)
                .employeeId("emp_456")
                .title("Broken Window")
                .description("Living room window is broken")
                .status("IN_PROGRESS")
                .createDate(Timestamp.valueOf(LocalDateTime.now().minusDays(1)))
                .build();

        // Setup test comments
        testComment1 = FacilityReportDetail.builder()
                .id(1)
                .facilityReport(testReport)
                .employeeId("emp_123")
                .comment("I've reported this issue")
                .createDate(Timestamp.valueOf(LocalDateTime.now()))
                .lastModificationDate(null)
                .build();

        testComment2 = FacilityReportDetail.builder()
                .id(2)
                .facilityReport(testReport)
                .employeeId("hr_456")
                .comment("Maintenance scheduled for tomorrow")
                .createDate(Timestamp.valueOf(LocalDateTime.now()))
                .lastModificationDate(Timestamp.valueOf(LocalDateTime.now().plusHours(1)))
                .build();
    }

    @Test
    void createFacilityReport_Success() {
        // Arrange
        CreateReportRequest request = new CreateReportRequest();
        request.setFacilityType("PLUMBING");
        request.setTitle("Leaking Faucet");
        request.setDescription("Kitchen faucet is leaking");
        request.setHouseId(1);
        request.setEmployeeId("emp_123");

        when(facilityRepository.findByHouseIdAndType(1, "PLUMBING"))
                .thenReturn(Optional.of(testFacility));
        when(facilityReportRepository.save(any(FacilityReport.class)))
                .thenAnswer(invocation -> {
                    FacilityReport savedReport = invocation.getArgument(0);
                    savedReport.setId(1); // Set ID as if database generated it
                    return savedReport;
                });

        // Act
        ReportResponse response = facilityReportService.createFacilityReport(1, request, "emp_123");

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getId());
        assertEquals("Leaking Faucet", response.getTitle());
        assertEquals("PLUMBING", response.getFacilityType());
        assertEquals("OPEN", response.getStatus());

        verify(facilityReportRepository, times(1)).save(any(FacilityReport.class));
    }

    @Test
    void createFacilityReport_FacilityNotFound() {
        // Arrange
        CreateReportRequest request = new CreateReportRequest();
        request.setFacilityType("PLUMBING");
        request.setTitle("Test Title");
        request.setDescription("Test Description");

        when(facilityRepository.findByHouseIdAndType(1, "PLUMBING"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(FacilityNotFoundException.class, () ->
                facilityReportService.createFacilityReport(1, request, "emp_123")
        );

        verify(facilityReportRepository, never()).save(any());
    }

    @Test
    void getFacilityReportById_Success() {
        // Arrange
        when(facilityReportRepository.findById(1))
                .thenReturn(Optional.of(testReport));

        // Act
        ReportResponse response = facilityReportService.getFacilityReportById(1);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getId());
        assertEquals("Leaking Faucet", response.getTitle());
        assertEquals("emp_123", response.getCreatedBy());
    }

    @Test
    void getFacilityReportById_NotFound() {
        // Arrange
        when(facilityReportRepository.findById(999))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ReportNotFoundException.class, () ->
                facilityReportService.getFacilityReportById(999)
        );
    }

    @Test
    void getFacilityReportByHouseId_Success() {
        // Arrange
        List<FacilityReport> reports = Arrays.asList(testReport);
        when(facilityReportRepository.findByHouseId(1))
                .thenReturn(reports);

        // Act
        ReportListResponse response = facilityReportService.getFacilityReportsByHouseId(1);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(1, response.getData().size());
        assertEquals("Leaking Faucet", response.getData().get(0).getTitle());
        verify(facilityReportRepository).findByHouseId(1);
    }

    @Test
    void getFacilityReportByHouseId_EmptyList() {
        // Arrange
        when(facilityReportRepository.findByHouseId(1))
                .thenReturn(Collections.emptyList());

        // Act
        ReportListResponse response = facilityReportService.getFacilityReportsByHouseId(1);

        // Assert
        assertTrue(response.isSuccess());
        assertTrue(response.getData().isEmpty());
        verify(facilityReportRepository).findByHouseId(1);
    }

    @Test
    void getFacilityReports_WithPagination_Success() {
        // Arrange
        Page<FacilityReport> reportPage = new PageImpl<>(
                Arrays.asList(testReport, testReport2),
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createDate")),
                2
        );

        when(facilityReportRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(reportPage);

        // Act
        Page<FacilityReport> result = facilityReportService.getFacilityReportsByHouseId(
                1, 0, 10, "createDate", "desc", null, null
        );

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals("Leaking Faucet", result.getContent().get(0).getTitle());
        assertEquals("Broken Window", result.getContent().get(1).getTitle());
        verify(facilityReportRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getFacilityReportDetails_WithComments() {
        // Arrange
        List<FacilityReportDetail> comments = Arrays.asList(testComment1, testComment2);

        when(facilityReportRepository.findById(1))
                .thenReturn(Optional.of(testReport));
        when(facilityReportDetailRepository.findByIdOrderByCreateDateDesc(1))
                .thenReturn(comments);

        // Act
        ReportDetailResponse response = facilityReportService.getFacilityReportDetails(1);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getId());
        assertEquals("Leaking Faucet", response.getTitle());
        assertEquals(2, response.getComments().size());

        // Check last modification date is from testComment2
        assertNotNull(response.getLastModificationDate());
        assertEquals(testComment2.getLastModificationDate(), response.getLastModificationDate());

        // Check comment details
        CommentDTO firstComment = response.getComments().get(0);
        assertEquals(1, firstComment.getCommentId());
        assertEquals("I've reported this issue", firstComment.getDescription());
        assertEquals(testComment1.getCreateDate(), firstComment.getCommentDate()); // No modification date

        CommentDTO secondComment = response.getComments().get(1);
        assertEquals(2, secondComment.getCommentId());
        assertEquals("Maintenance scheduled for tomorrow", secondComment.getDescription());
        assertEquals(testComment2.getLastModificationDate(), secondComment.getCommentDate()); // Has modification date
    }

    @Test
    void getFacilityReportDetails_NoComments() {
        // Arrange
        when(facilityReportRepository.findById(1))
                .thenReturn(Optional.of(testReport));
        when(facilityReportDetailRepository.findByIdOrderByCreateDateDesc(1))
                .thenReturn(Collections.emptyList());

        // Act
        ReportDetailResponse response = facilityReportService.getFacilityReportDetails(1);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getId());
        assertTrue(response.getComments().isEmpty());
        assertEquals(testReport.getCreateDate(), response.getLastModificationDate()); // Falls back to report create date
    }

    @Test
    void getFacilityReportDetails_ReportNotFound() {
        // Arrange
        when(facilityReportRepository.findById(999))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ReportNotFoundException.class, () ->
                facilityReportService.getFacilityReportDetails(999)
        );

        verify(facilityReportDetailRepository, never()).findByIdOrderByCreateDateDesc(any());
    }

    @Test
    void addComment_Success() {
        // Arrange
        Integer reportId = 1;
        String employeeId = "emp_456";
        String description = "This issue needs urgent attention";

        when(facilityReportRepository.findById(reportId))
                .thenReturn(Optional.of(testReport));

        when(facilityReportDetailRepository.save(any(FacilityReportDetail.class)))
                .thenAnswer(invocation -> {
                    FacilityReportDetail savedComment = invocation.getArgument(0);
                    savedComment.prePersist();
                    savedComment.setId(3); // Set ID as if database generated it
                    // @PrePersist has already set both timestamps
                    return savedComment;
                });

        // Act
        Integer commentId = facilityReportService.addComment(reportId, employeeId, description);

        // Assert
        assertNotNull(commentId);
        assertEquals(3, commentId);

        verify(facilityReportRepository, times(1)).findById(reportId);

        // Capture the saved entity
        ArgumentCaptor<FacilityReportDetail> captor = ArgumentCaptor.forClass(FacilityReportDetail.class);
        verify(facilityReportDetailRepository, times(1)).save(captor.capture());

        FacilityReportDetail savedDetail = captor.getValue();
        assertEquals(reportId, savedDetail.getFacilityReport().getId());
        assertEquals(employeeId, savedDetail.getEmployeeId());
        assertEquals(description, savedDetail.getComment());

        // @PrePersist sets both timestamps before save
        assertNotNull(savedDetail.getCreateDate());
        assertNotNull(savedDetail.getLastModificationDate());

        // Both timestamps should be equal for a new comment
        assertEquals(savedDetail.getCreateDate(), savedDetail.getLastModificationDate());
    }

    @Test
    void addComment_ReportNotFound() {
        // Arrange
        Integer reportId = 999;
        String employeeId = "emp_456";
        String description = "Comment on non-existent report";

        when(facilityReportRepository.findById(reportId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ReportNotFoundException.class, () ->
                facilityReportService.addComment(reportId, employeeId, description)
        );

        verify(facilityReportDetailRepository, never()).save(any());
    }

    @Test
    void updateStatus_Success() {
        // Arrange
        when(facilityReportRepository.findById(1))
                .thenReturn(Optional.of(testReport));
        when(facilityReportRepository.save(any(FacilityReport.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        UpdateReportStatusResponse response = facilityReportService.updateStatus(1, "IN_PROGRESS");

        // Assert
        assertTrue(response.isSuccess());
        assertEquals("Status updated successfully", response.getMessage());
        assertEquals(1, response.getReportId());
        assertEquals("OPEN", response.getPreviousStatus());
        assertEquals("IN_PROGRESS", response.getNewStatus());
        assertNotNull(response.getUpdatedAt());

        // Verify the report was saved with new status
        ArgumentCaptor<FacilityReport> captor = ArgumentCaptor.forClass(FacilityReport.class);
        verify(facilityReportRepository).save(captor.capture());
        assertEquals("IN_PROGRESS", captor.getValue().getStatus());
    }


}