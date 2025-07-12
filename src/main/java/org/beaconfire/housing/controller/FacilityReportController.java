package org.beaconfire.housing.controller;

import org.beaconfire.housing.dto.response.ReportDTO;
import org.beaconfire.housing.service.FacilityReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
