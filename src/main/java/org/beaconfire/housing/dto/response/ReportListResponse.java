package org.beaconfire.housing.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.beaconfire.housing.entity.FacilityReport;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportListResponse {
    private boolean success = true;
    private List<ReportResponse> data;
}
