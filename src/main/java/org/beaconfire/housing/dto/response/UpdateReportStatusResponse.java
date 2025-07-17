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
public class UpdateReportStatusResponse {
    private boolean success;
    private String message;
    private Integer reportId;
    private String previousStatus;
    private String newStatus;
    private Timestamp updatedAt;
}
