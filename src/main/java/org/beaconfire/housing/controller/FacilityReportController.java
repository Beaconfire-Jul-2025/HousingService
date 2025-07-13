package org.beaconfire.housing.controller;

import org.beaconfire.housing.dto.request.CreateReportRequestDTO;
import org.beaconfire.housing.dto.response.CreateReportResponseDTO;
import org.beaconfire.housing.dto.response.ReportDTO;
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
    @GetMapping("/facility-report/{id}")
    public ResponseEntity<ReportDTO> getFacilityReportById(@PathVariable Integer id) {
        ReportDTO report = facilityReportService.getFacilityReportById(id);
        return new ResponseEntity<>(report, HttpStatus.OK);
    }

    // create facility report for a house
    @PostMapping("/facility-report")
    public ResponseEntity<CreateReportResponseDTO> createFacilityReport(
            @Valid @RequestBody CreateReportRequestDTO request,
            Authentication authentication) {

        // Get houseId, employeeId from request body
        Integer houseId = request.getHouseId();
        String employeeId = request.getEmployeeId();

        ReportDTO report = facilityReportService.createFacilityReport(houseId, request, employeeId);

        // response dto
        CreateReportResponseDTO response = new CreateReportResponseDTO(
                report.getId(),
                "Facility report submitted"
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
