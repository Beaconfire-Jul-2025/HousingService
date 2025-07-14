package org.beaconfire.housing.service;

import org.beaconfire.housing.dto.CommentDTO;
import org.beaconfire.housing.dto.request.CreateReportRequest;
import org.beaconfire.housing.dto.response.ReportDetailResponse;
import org.beaconfire.housing.dto.response.ReportListResponse;
import org.beaconfire.housing.dto.response.ReportResponse;
import org.beaconfire.housing.entity.Facility;
import org.beaconfire.housing.entity.FacilityReport;
import org.beaconfire.housing.entity.FacilityReportDetail;
import org.beaconfire.housing.exception.BadRequestException;
import org.beaconfire.housing.exception.CommentNotFoundException;
import org.beaconfire.housing.exception.FacilityNotFoundException;
import org.beaconfire.housing.exception.ReportNotFoundException;
import org.beaconfire.housing.repo.FacilityReportDetailRepository;
import org.beaconfire.housing.repo.FacilityReportRepository;
import org.beaconfire.housing.repo.FacilityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FacilityReportService {

    private final FacilityRepository facilityRepository;
    private final FacilityReportRepository facilityReportRepository;
    private final FacilityReportDetailRepository facilityReportDetailRepository;

    public FacilityReportService(FacilityRepository facilityRepository,
                                 FacilityReportRepository facilityReportRepository,
                                 FacilityReportDetailRepository facilityReportDetailRepository) {
        this.facilityRepository = facilityRepository;
        this.facilityReportRepository = facilityReportRepository;
        this.facilityReportDetailRepository = facilityReportDetailRepository;
    }

    // Create a new report
    @Transactional
    public ReportResponse createFacilityReport(Integer houseId, CreateReportRequest request, String employeeId) {
        // Find facility by house and type
        Facility facility = facilityRepository.findByHouseIdAndType(houseId, request.getFacilityType())
                .orElseThrow(() -> new FacilityNotFoundException(
                        "Facility " + request.getFacilityType() + " not found in this house"));

        // Create report
        FacilityReport facilityReport = FacilityReport.builder()
                .facility(facility)
                .employeeId(employeeId)
                .title(request.getTitle())
                .description(request.getDescription())
                .status("Open") // Default status when report is created
                .build();

        // Save report
        facilityReportRepository.save(facilityReport);

        // Return report
        return convertToReportDTO(facilityReport);
    }

    // Get report summary by report id
    public ReportResponse getFacilityReportById(Integer id) {
        FacilityReport report = facilityReportRepository.findById(id)
                .orElseThrow(() -> new ReportNotFoundException("Report not found"));
        return convertToReportDTO(report);

    }

    // Get report list by house id
    public ReportListResponse getFacilityReportByHouseId(Integer houseId) {
        // Fetch list of reports
        List<FacilityReport> reports = facilityReportRepository.findByHouseIdOrderByCreateDateDesc(houseId);
        // Map to DTOs
        List<ReportResponse> reportResponses = reports.stream()
                .map(this::convertToReportDTO)
                .collect(Collectors.toList());
        return new ReportListResponse(true, reportResponses);
    }

    // Get details for one report
    public ReportDetailResponse getFacilityReportDetails(Integer id) {
        FacilityReport report = facilityReportRepository.findById(id)
                .orElseThrow(() -> new ReportNotFoundException("Report not found"));
        // fetch comments
        List<FacilityReportDetail> comments = facilityReportDetailRepository.findByIdOrderByCreateDateDesc(id);

        // calculate the last modification date from all the comments
        // get the lastest modification date or show the created date
        Timestamp lastModificationDate = comments.stream()
                .map(FacilityReportDetail::getLastModificationDate)
                .filter(Objects::nonNull)
                .max(Timestamp::compareTo)
                .orElse(report.getCreateDate());

        // map to detail dto
        return convertToReportDetailResponse(report, comments, lastModificationDate);
    }

    // Add comment
    public Integer addComment(Integer reportId, String employeeId, String description) {
        // verify if the report exists
        FacilityReport report = facilityReportRepository.findById(reportId)
                .orElseThrow(() -> new ReportNotFoundException("Report not found"));
        // create comments
        FacilityReportDetail comment = FacilityReportDetail.builder()
                .facilityReport(report)
                .employeeId(employeeId)
                .comment(description)
                .build();
        // save and return id
        FacilityReportDetail savedComment = facilityReportDetailRepository.save(comment);
        return savedComment.getId();
    }

    // Update comment
    public void updateComment(Integer reportId, Integer commentId, String description, String employeeId) {
        // verify if the report exists
        FacilityReport report = facilityReportRepository.findById(reportId)
                .orElseThrow(() -> new ReportNotFoundException("Report not found"));

        // Get the comment by id
        FacilityReportDetail comment = facilityReportDetailRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found"));

        // Verify comment belongs to this report
        if(!comment.getFacilityReport().getId().equals(reportId)) {
            throw new BadRequestException("Comment does not belong to this report");
        }

        // Verify user owns the comment
        if(!comment.getEmployeeId().equals(employeeId)) {
            throw new BadRequestException("Comment does not belong to this user");
        }

        // Update comment
        comment.setComment(description);

        facilityReportDetailRepository.save(comment);
    }
    // Helper function: Convert entity to DTO
    private ReportResponse convertToReportDTO(FacilityReport report) {
        return ReportResponse.builder()
                .id(report.getId())
                .facilityType(report.getFacility().getType())
                .title(report.getTitle())
                .description(report.getDescription())
                .status(report.getStatus())
                .createdBy(report.getEmployeeId())
                .createDate(report.getCreateDate())
                .build();
    }

    private ReportDetailResponse convertToReportDetailResponse(
            FacilityReport report,
            List<FacilityReportDetail> comments,
            Timestamp lastModificationDate) {
        return ReportDetailResponse.builder()
                .id(report.getId())
                .facilityType(report.getFacility().getType())
                .title(report.getTitle())
                .description(report.getDescription())
                .status(report.getStatus())
                .createdBy(report.getEmployeeId())
                .createDate(report.getCreateDate())
                .lastModificationDate(lastModificationDate)
                .comments(convertToCommentDTOs(comments))
                .build();
    }

    private List<CommentDTO> convertToCommentDTOs(List<FacilityReportDetail> details) {
        return details.stream()
                .map(detail -> CommentDTO.builder()
                        .commentId(detail.getId())
                        .createdBy(detail.getEmployeeId())
                        .commentDate(detail.getLastModificationDate() != null ?
                                detail.getLastModificationDate() : detail.getCreateDate())
                        .description(detail.getComment())
                        .build())
                .collect(Collectors.toList());
    }
}