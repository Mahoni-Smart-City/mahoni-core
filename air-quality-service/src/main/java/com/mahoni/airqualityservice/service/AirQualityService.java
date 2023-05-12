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
}
