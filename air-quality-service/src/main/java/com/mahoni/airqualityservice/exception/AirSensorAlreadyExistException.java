package com.mahoni.airqualityservice.exception;

public class AirSensorAlreadyExistException extends RuntimeException {
  public AirSensorAlreadyExistException(Long id) {
    super("Air Sensor with id " + id + " is already exists");
  }
}
