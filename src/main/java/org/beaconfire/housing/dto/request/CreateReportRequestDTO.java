package org.beaconfire.housing.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateReportRequestDTO {
    @NotBlank(message = "Facility Type is required")
    private String facilityType;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Employee ID is required")
    private String employeeId;

    @NotNull(message = "House ID is required")
    private Integer houseId;
}
