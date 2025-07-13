package org.beaconfire.housing.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private boolean success = false;
    private String message;
    private int status;
}
