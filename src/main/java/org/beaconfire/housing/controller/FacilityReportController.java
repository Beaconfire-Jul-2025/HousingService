package org.beaconfire.housing.controller;

import org.beaconfire.housing.dto.request.CreateReportRequest;
import org.beaconfire.housing.dto.response.CreateReportResponse;
import org.beaconfire.housing.dto.response.ReportDetailResponse;
import org.beaconfire.housing.dto.response.ReportListResponse;
import org.beaconfire.housing.dto.response.ReportResponse;
import org.beaconfire.housing.service.FacilityReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/housing")
public class FacilityReportController {
    private final FacilityReportService facilityReportService;

    public FacilityReportController(FacilityReportService facilityReportService) {
        this.facilityReportService = facilityReportService;
    }

    // get report by report id
    @GetMapping("/facility-report/{reportId}")
    public ResponseEntity<ReportResponse> getFacilityReportById(@PathVariable Integer reportId) {
        ReportResponse report = facilityReportService.getFacilityReportById(reportId);
        return new ResponseEntity<>(report, HttpStatus.OK);
    }

    // get report detail by report id (detail contains comments)
    @GetMapping("/facility-report/{reportId}/details")
    public ResponseEntity<ReportDetailResponse> getFacilityReportDetails(@PathVariable Integer reportId) {
        ReportDetailResponse response = facilityReportService.getFacilityReportDetails(reportId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // get a list of reports for specific house
    @GetMapping("/facility-report")
    public ResponseEntity<ReportListResponse> getFacilityReportByHouseId(@RequestParam("houseId") Integer houseId) {
        ReportListResponse response = facilityReportService.getFacilityReportByHouseId(houseId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // create facility report for a house
    @PostMapping("/facility-report")
    public ResponseEntity<CreateReportResponse> createFacilityReport(
            @Valid @RequestBody CreateReportRequest request,
            Authentication authentication) {

        // Get houseId, employeeId from request body
        Integer houseId = request.getHouseId();
        String employeeId = request.getEmployeeId();

        ReportResponse report = facilityReportService.createFacilityReport(houseId, request, employeeId);

        // response dto
        CreateReportResponse response = new CreateReportResponse(
                report.getId(),
                "Facility report submitted"
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
