package com.mahoni.airqualityservice.controller;

import com.mahoni.airqualityservice.dto.AirQualityResponse;
import com.mahoni.airqualityservice.exception.AirSensorNotFoundException;
import com.mahoni.airqualityservice.service.AirQualityService;
import com.mahoni.schema.AirQualityProcessedSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/air-quality")
@Slf4j
public class AirQualityController {

  @Autowired
  AirQualityService airQualityService;

  @GetMapping("/id/{id}")
  public ResponseEntity<AirQualityResponse> getAqiById(@PathVariable("id") Long id) {
    try {
      AirQualityResponse response = mapper(airQualityService.getAqi(id));
      return ResponseEntity.ok(response);
    } catch (AirSensorNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @GetMapping("/loc/{loc}")
  public ResponseEntity<List<AirQualityResponse>> getAqiByLoc(@PathVariable("loc") String loc) {
    try {
      return ResponseEntity.ok(airQualityService.getAqiByLocation(loc).stream().map(this::mapper).collect(Collectors.toList()));
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
