package org.beaconfire.housing.dto.request;


import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class FacilityRequest {
    @NotBlank(message = "type required")
    private String type;
    @NotNull(message = "quantity required")
    private Integer quantity;
    private String description;
    @NotNull(message = "houseId required")
    private Integer houseId;
}
