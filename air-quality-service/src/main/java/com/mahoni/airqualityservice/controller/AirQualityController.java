package com.mahoni.airqualityservice.controller;

import com.mahoni.airqualityservice.dto.AirQualityResponse;
import com.mahoni.airqualityservice.exception.AirSensorNotFoundException;
import com.mahoni.airqualityservice.model.Location;
import com.mahoni.airqualityservice.repository.LocationRepository;
import com.mahoni.airqualityservice.service.AirQualityService;
import com.mahoni.schema.AirQualityProcessedSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/air-quality")
@Slf4j
public class AirQualityController {

  @Autowired
  AirQualityService airQualityService;

  @Autowired
  LocationRepository locationRepository;

  @GetMapping("/id/{id}")
  public ResponseEntity<AirQualityResponse> get(@PathVariable("id") Long id) {
    try {
      AirQualityResponse response = mapper(airQualityService.getAqi(id));
      return ResponseEntity.ok(response);
    } catch (AirSensorNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @GetMapping("/loc/{loc}")
  public ResponseEntity<List<AirQualityResponse>> getByLoc(@PathVariable("loc") String loc) {
    try {
      return ResponseEntity.ok(airQualityService.getAqiByLocation(loc).stream().map(this::mapper).collect(Collectors.toList()));
    } catch (AirSensorNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @GetMapping("/coordinate")
  public ResponseEntity<AirQualityResponse> getByCoordinate(
    @RequestParam("longitude") Double longitude,
    @RequestParam("latitude") Double latitude) {
    try {
      return ResponseEntity.ok(mapper(airQualityService.getAqiByCoordinate(longitude, latitude)));
    } catch (AirSensorNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @GetMapping("/history/{id}")
  public ResponseEntity<Map<String, AirQualityResponse>> getList(@PathVariable("id") Long id) {
    Map<String, AirQualityProcessedSchema> history = airQualityService.history(id);
    Map<String, AirQualityResponse> response = new HashMap<>();
    for (Map.Entry<String, AirQualityProcessedSchema> entrySet : history.entrySet()) {
      response.put(entrySet.getKey(), mapper(entrySet.getValue()));
    }
    return ResponseEntity.ok(response);
  }

  private AirQualityResponse mapper(AirQualityProcessedSchema schema) {
    if (schema != null) {
      return new AirQualityResponse(
        schema.getSensorId(),
        schema.getTimestamp(),
        schema.getAqi(),
        schema.getCategory(),
        schema.getCo(),
        schema.getNo(),
        schema.getNo2(),
        schema.getO3(),
        schema.getSo2(),
        schema.getPm25(),
        schema.getPm10(),
        schema.getNh3(),
        schema.getPressure(),
        schema.getHumidity(),
        schema.getTemperature(),
        locationRepository.findById(schema.getIdLocation()).orElseGet(Location::new),
        Recommendation.recommendation.get(schema.getCategory())
      );
    } else {
      return null;
    }
  }

  static class Recommendation {
    static final Map<String, List<String>> recommendation = Map.of(
      "Good", new ArrayList<>(List.of(
        "Great day to be active outside"
      )),
      "Moderate", new ArrayList<>(List.of(
        "Good day to be active outside",
        "Peoples who are unusually sensitive to air pollution could have symptoms"
      )),
      "Unhealthy for Sensitive Groups", new ArrayList<>(List.of(
        "It’s OK to be active outside",
        "Watch for symptoms and take action as needed"
      )),
      "Unhealthy", new ArrayList<>(List.of(
        "For all outdoor activities, take more breaks and do less intense activities",
        "Watch for symptoms and take action as needed"
      )),
      "Hazardous", new ArrayList<>(List.of(
        "Move all activities indoors or reschedule them to another day"
      )),
      "", new ArrayList<>(List.of(
        ""
      ))
    );
  }
}
