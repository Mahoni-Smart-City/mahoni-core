package com.mahoni.airqualityservice.exception;

import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AirSensorAlreadyExistException extends RuntimeException {
  public AirSensorAlreadyExistException(@NotNull Long id) {
    super("Air Sensor with id " + id + " is already exists");
  }
}
