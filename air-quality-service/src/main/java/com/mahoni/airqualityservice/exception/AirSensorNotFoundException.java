package com.mahoni.airqualityservice.exception;

public class AirSensorNotFoundException extends RuntimeException {
  public AirSensorNotFoundException(Long id) {
    super("Air Sensor with id " + id + " is not found");
  }
}
