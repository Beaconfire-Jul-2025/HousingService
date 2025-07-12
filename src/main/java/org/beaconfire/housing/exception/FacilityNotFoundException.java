package org.beaconfire.housing.exception;

public class FacilityNotFoundException extends RuntimeException {
  public FacilityNotFoundException(String message) {
    super(message);
  }
}
