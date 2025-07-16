package org.beaconfire.housing.controller;

import org.beaconfire.housing.dto.PageListResponse;
import org.beaconfire.housing.dto.request.CreateCommentRequest;
import org.beaconfire.housing.dto.request.CreateReportRequest;
import org.beaconfire.housing.dto.response.*;
import org.beaconfire.housing.entity.FacilityReport;
import org.beaconfire.housing.exception.BadRequestException;
import org.beaconfire.housing.service.FacilityReportService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/housing")
public class FacilityReportController {
    private final FacilityReportService facilityReportService;

    public FacilityReportController(FacilityReportService facilityReportService) {
        this.facilityReportService = facilityReportService;
    }

    // get report by report id
    public ReportResponse getFacilityReportById(@PathVariable Integer reportId) {
        ReportResponse report = facilityReportService.getFacilityReportById(reportId);
        return report;
    }

    // get report detail by report id (detail contains comments)
    @GetMapping("/facility-report/{reportId}/details")
    public ReportDetailResponse getFacilityReportDetails(@PathVariable Integer reportId) {
        ReportDetailResponse response = facilityReportService.getFacilityReportDetails(reportId);
        return response;
    }

    // get a list of reports for specific house
    @GetMapping("/facility-report")
    @PreAuthorize("hasRole('ROLE_HR')")
    public PageListResponse<FacilityReport> getFacilityReportsByHouseId(
            @RequestParam("houseId") Integer houseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String title
    ) {
        Page<FacilityReport> reportPage = facilityReportService.getFacilityReportsByHouseId(houseId, page, size, sortBy, sortDir, status, title);
        return PageListResponse.<FacilityReport>builder()
                .list(reportPage.getContent())
                .current(reportPage.getNumber() + 1)
                .pageSize(reportPage.getSize())
                .total(reportPage.getTotalElements())
                .build();
    }

    // create facility report for a house
    @PostMapping("/facility-report")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateReportResponse createFacilityReport(
            @Valid @RequestBody CreateReportRequest request,
            Authentication authentication) {

        // Get houseId from request body
        Integer houseId = request.getHouseId();
        // Get employee id from authentication
        String employeeId = authentication.getPrincipal().toString();

        ReportResponse report = facilityReportService.createFacilityReport(houseId, request, employeeId);

        // response dto
        return new CreateReportResponse(
                report.getId(),
                "Facility report submitted"
        );
    }

    // create comment for reports
    @PostMapping("/facility-report/{reportId}/comment")
    public CreateCommentResponse createComment(
            @PathVariable Integer reportId,
            @Valid @RequestBody CreateCommentRequest request,
            Authentication authentication ){
        String employeeId = authentication.getPrincipal().toString();
        Integer commentId = facilityReportService.addComment(reportId, employeeId, request.getDescription());

        return new CreateCommentResponse(
                commentId,
                "Comment added successfully"
        );
    }

    // update the status
    @PatchMapping("/facility-report/{reportId}/status")
    @PreAuthorize("hasRole('ROLE_HR')")
    public UpdateReportStatusResponse updateFacilityReportStatus(
            @PathVariable Integer reportId,
            @RequestParam @NotBlank(message = "Status is required") String status) {

        // Validate status
        if (!isValidStatus(status)) {
            throw new BadRequestException("Invalid status. Must be one of: 'OPEN', 'IN_PROGRESS', 'CLOSED'");
        }

        UpdateReportStatusResponse response = facilityReportService.updateStatus(
                reportId,
                status
        );
        return response;
    }

    private boolean isValidStatus(String status) {
        return status != null && status.matches("^(OPEN|IN_PROGRESS|CLOSED)$");
    }
}
