package com.mahoni.airqualityservice.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AirSensorNotFoundException extends RuntimeException {
  public AirSensorNotFoundException(Long id) {
    super("Air Sensor with id " + id + " is not found");
  }
}
