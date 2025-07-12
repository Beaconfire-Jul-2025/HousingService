package org.beaconfire.housing.dto;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class HouseDTO {

    @NotNull(message = "Landlord ID is required")
    private Integer landlordId;

    @NotBlank(message = "Address is required")
    @Size(max = 500)
    private String address;

    @NotNull(message = "Max Occupant is required")
    @Min(1)
    private Integer maxOccupant;

    private String description;
}