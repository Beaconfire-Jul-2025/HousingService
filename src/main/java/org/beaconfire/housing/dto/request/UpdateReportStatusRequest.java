package org.beaconfire.housing.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateReportStatusRequest {
    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^('OPEN'|'IN_PROGRESS'|'CLOSED')$",
            message = "Status must be one of: 'OPEN', 'IN_PROGRESS', 'CLOSED'")
    private String status;;
}
