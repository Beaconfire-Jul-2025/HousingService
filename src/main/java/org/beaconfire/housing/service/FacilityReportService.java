package org.beaconfire.housing.service;

import org.beaconfire.housing.dto.request.CreateReportRequestDTO;
import org.beaconfire.housing.dto.response.ReportDTO;
import org.beaconfire.housing.entity.Facility;
import org.beaconfire.housing.entity.FacilityReport;
import org.beaconfire.housing.exception.FacilityNotFoundException;
import org.beaconfire.housing.exception.ReportNotFoundException;
import org.beaconfire.housing.repo.FacilityReportRepository;
import org.beaconfire.housing.repo.FacilityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FacilityReportService {

    private final FacilityRepository facilityRepository;
    private final FacilityReportRepository facilityReportRepository;

    public FacilityReportService(FacilityRepository facilityRepository,
                                 FacilityReportRepository facilityReportRepository) {
        this.facilityRepository = facilityRepository;
        this.facilityReportRepository = facilityReportRepository;
    }

    // Create a new report
    @Transactional
    public ReportDTO createFacilityReport(Integer houseId, CreateReportRequestDTO request, String employeeId) {
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

    // Get report by report id
    public ReportDTO getFacilityReportById(Integer id) {
        FacilityReport report = facilityReportRepository.findById(id)
                .orElseThrow(() -> new ReportNotFoundException("Report not found"));
        return convertToReportDTO(report);

    }


    // Helper function: Convert entity to DTO
    private ReportDTO convertToReportDTO(FacilityReport report) {
        return ReportDTO.builder()
                .id(report.getId())
                .facilityName(report.getFacility().getType())
                .title(report.getTitle())
                .description(report.getDescription())
                .status(report.getStatus())
                .createdBy(report.getEmployeeId())
                .createDate(report.getCreateDate())
                .build();
    }

}