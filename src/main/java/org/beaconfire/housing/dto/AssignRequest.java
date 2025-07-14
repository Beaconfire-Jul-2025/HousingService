package org.beaconfire.housing.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignRequest {
    private String userId;
    private String houseId;
}
