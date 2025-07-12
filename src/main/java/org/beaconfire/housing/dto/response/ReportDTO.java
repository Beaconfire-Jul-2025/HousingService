package org.beaconfire.housing.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportDTO {
    private Integer id;
    private String facilityName;  // e.g., "Chair"
    private String title;
    private String description;
    private String status;  // "OPEN", "IN_PROGRESS", "CLOSED"
    private String createdBy;  // Employee ID who created it
    private Timestamp createDate;
}
