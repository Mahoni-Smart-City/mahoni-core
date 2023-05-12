package com.mahoni.airqualityservice.service;

import com.mahoni.airqualityservice.exception.AirSensorNotFoundException;
import com.mahoni.airqualityservice.kafka.AirQualityServiceStream;
import com.mahoni.airqualityservice.model.AirSensor;
import com.mahoni.airqualityservice.repository.AirSensorRepository;
import com.mahoni.airqualityservice.repository.LocationRepository;
import com.mahoni.schema.AirQualityProcessedSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AirQualityService {
  @Autowired
  AirSensorRepository airSensorRepository;

  @Autowired
  LocationRepository locationRepository;

  @Autowired
  AirQualityServiceStream airQualityServiceStream;

  public AirQualityProcessedSchema getAqi(Long sensorId) {
    return airQualityServiceStream.get(AirQualityServiceStream.parseTableKey(sensorId));
  }

  public List<AirQualityProcessedSchema> getAqiByLocation(String location) {
    Optional<List<AirSensor>> airSensors = airSensorRepository.findAirSensorsByLocation(location);
    if (airSensors.isEmpty()) {
      throw new AirSensorNotFoundException();
    }
    return airSensors.get().stream().map(AirSensor::getId).map(this::getAqi).collect(Collectors.toList());
  }

  public AirQualityProcessedSchema getAqiByCoordinate(Double longitude, Double latitude) {
    AirSensor airSensor = findShortestSensor(longitude, latitude);
    Long sensorId = airSensor.getId();
    return getAqi(sensorId);
  }

  public HashMap<String, AirQualityProcessedSchema> history(Long sensorId) {
    List<String> keys = new LinkedList<>();
    for (DayOfWeek day: DayOfWeek.values()) {
      for (int i = 0; i < 24 ; i++) {
        keys.add(day.name() + ":" + i + ":" + sensorId);
      }
    }

    HashMap<String, AirQualityProcessedSchema> history = new HashMap<>();
    for (String key: keys) {
      history.put(key, airQualityServiceStream.get(key));
    }
    return history;
  }

  private AirSensor findShortestSensor(Double longitude, Double latitude) {
    AirSensor airSensor = new AirSensor();
    Double distance = Double.MAX_VALUE;
    for (AirSensor sensor: airSensorRepository.findAll()) {
      Double currentDistance = calculateDistance(longitude, latitude, sensor.getLocation().getLongitude(), sensor.getLocation().getLatitude());
      if (currentDistance <= distance) {
        airSensor = sensor;
        distance = currentDistance;
      }
    }
    return airSensor;
  }

  private static Double calculateDistance(Double longitude1, Double latitude1, Double longitude2, Double latitude2) {
    Double x = longitude1 - longitude2;
    Double y = latitude1 - latitude2;
    return Math.sqrt(x * x + y * y);
  }
}
