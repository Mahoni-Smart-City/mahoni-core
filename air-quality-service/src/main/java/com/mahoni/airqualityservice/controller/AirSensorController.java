package com.mahoni.airqualityservice.controller;

import com.mahoni.airqualityservice.dto.AirQualityResponse;
import com.mahoni.airqualityservice.dto.AirSensorRequest;
import com.mahoni.airqualityservice.exception.AirSensorNotFoundException;
import com.mahoni.airqualityservice.model.AirSensor;
import com.mahoni.airqualityservice.service.AirSensorService;
import com.mahoni.schema.AirQualityProcessedSchema;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/air-sensors")
@Slf4j
public class AirSensorController {

  @Autowired
  AirSensorService airSensorService;

  @PostMapping
  public ResponseEntity<AirSensor> post(@Valid @RequestBody AirSensorRequest request) {
    try {
      AirSensor newAirSensor = airSensorService.create(request);
      return ResponseEntity.ok(newAirSensor);
    } catch (RuntimeException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

  @GetMapping
  public ResponseEntity<List<AirSensor>> getAll() {
    List<AirSensor> allAirSensors = airSensorService.getAll();
    return ResponseEntity.ok(allAirSensors);
  }

  @GetMapping("/{id}")
  public ResponseEntity<AirSensor> get(@PathVariable("id") Long id) {
    try {
       AirSensor airSensor = airSensorService.getById(id);
       return ResponseEntity.ok(airSensor);
    } catch (AirSensorNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<AirSensor> delete(@PathVariable("id") Long id) {
    try {
      AirSensor airSensor = airSensorService.deleteById(id);
      return ResponseEntity.ok(airSensor);
    } catch (AirSensorNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<AirSensor> update(@PathVariable("id") Long id, @Valid @RequestBody AirSensorRequest request) {
    try {
      AirSensor updatedAirSensor = airSensorService.update(id, request);
      return ResponseEntity.ok(updatedAirSensor);
    } catch (AirSensorNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @GetMapping("/{id}/aqi")
  public ResponseEntity<AirQualityResponse> getAqi(@PathVariable("id") Long id) {
    try {
      AirQualityResponse response = mapper(airSensorService.getAqi(id));
      return ResponseEntity.ok(response);
    } catch (AirSensorNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  private AirQualityResponse mapper(AirQualityProcessedSchema schema) {
    return new AirQualityResponse(
      Long.parseLong(schema.getEventId()),
      schema.getSensorId(),
      schema.getTimestamp(),
      schema.getAqi(),
      schema.getNo2(),
      schema.getPm10(),
      schema.getPm25()
    );
  }
}
