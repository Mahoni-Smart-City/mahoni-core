package com.mahoni.airqualityservice.service;

import com.mahoni.airqualityservice.dto.AirSensorRequest;
import com.mahoni.airqualityservice.exception.AirSensorAlreadyExistException;
import com.mahoni.airqualityservice.exception.AirSensorNotFoundException;
import com.mahoni.airqualityservice.exception.LocationNotFoundException;
import com.mahoni.airqualityservice.kafka.AirQualityServiceStream;
import com.mahoni.airqualityservice.model.AirSensor;
import com.mahoni.airqualityservice.model.Location;
import com.mahoni.airqualityservice.repository.AirSensorRepository;
import com.mahoni.airqualityservice.repository.LocationRepository;
import com.mahoni.schema.AirQualityProcessedSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@Slf4j
public class AirSensorService {

  @Autowired
  AirSensorRepository airSensorRepository;

  @Autowired
  LocationRepository locationRepository;

  @Autowired
  AirQualityServiceStream airQualityServiceStream;

  public AirSensor create(AirSensorRequest airSensor) {
    Optional<Location> location = locationRepository.findById(airSensor.getIdLocation());
    if (airSensorRepository.findById(airSensor.getId()).isPresent()) {
      throw new AirSensorAlreadyExistException(airSensor.getId());
    }
    if (location.isEmpty()) {
      throw new LocationNotFoundException(airSensor.getIdLocation());
    }
    return airSensorRepository.save(new AirSensor(
      airSensor.getId(),
      airSensor.getNameLocation(),
      location.get()
    ));
  }

  public AirSensor getById(Long id) {
    Optional<AirSensor> airSensor = airSensorRepository.findById(id);
    if (airSensor.isEmpty()) {
      throw new AirSensorNotFoundException(id);
    }
    return airSensor.get();
  }

  public List<AirSensor> getAll() {
    return airSensorRepository.findAll();
  }

  public AirSensor deleteById(Long id) {
    Optional<AirSensor> airSensor = airSensorRepository.findById(id);
    if (airSensor.isEmpty()) {
      throw new AirSensorNotFoundException(id);
    }
    airSensorRepository.deleteById(id);
    return airSensor.get();
  }

  public AirSensor update(Long id, AirSensorRequest newAirSensor) {
    Optional<AirSensor> airSensor = airSensorRepository.findById(id);
    if (airSensor.isEmpty()) {
      throw new AirSensorNotFoundException(id);
    }
    Optional<Location> location = locationRepository.findById(newAirSensor.getIdLocation());
    if (location.isEmpty()) {
      throw new LocationNotFoundException(newAirSensor.getIdLocation());
    }
    AirSensor updatedAirSensor = airSensor.get();
    updatedAirSensor.setId(newAirSensor.getId());
    updatedAirSensor.setNameLocation(newAirSensor.getNameLocation());
    updatedAirSensor.setLocation(location.get());
    return airSensorRepository.save(updatedAirSensor);
  }

  public HashMap<String, AirQualityProcessedSchema> history(Long sensorId) {
    List<String> keys = new LinkedList<>();
    for (DayOfWeek day: DayOfWeek.values()) {
      for (int i = 0; i < 24 ; i++) {
        keys.add(day.name() + ":" + i + sensorId);
      }
    }

    HashMap<String, AirQualityProcessedSchema> history = new HashMap<>();
    for (String key: keys) {
      history.put(key, airQualityServiceStream.get(key));
    }
    return history;
  }

  public AirQualityProcessedSchema getAqi(Long sensorId) {
    return airQualityServiceStream.get(AirQualityServiceStream.parseTableKey(sensorId));
  }
}
