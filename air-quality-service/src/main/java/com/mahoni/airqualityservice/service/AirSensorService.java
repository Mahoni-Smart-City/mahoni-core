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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    Optional<Location> location = locationRepository.findById(Long.parseLong(airSensor.getLocationId()));
    if (airSensorRepository.findById(Long.parseLong(airSensor.getId())).isPresent()) {
      throw new AirSensorAlreadyExistException(Long.parseLong(airSensor.getId()));
    }
    if (location.isEmpty()) {
      throw new LocationNotFoundException(Long.parseLong(airSensor.getLocationId()));
    }
    return airSensorRepository.save(new AirSensor(
      Long.parseLong(airSensor.getId()),
      airSensor.getLocationName(),
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
    Optional<Location> location = locationRepository.findById(Long.parseLong(newAirSensor.getLocationId()));
    if (location.isEmpty()) {
      throw new LocationNotFoundException(Long.parseLong(newAirSensor.getLocationId()));
    }
    AirSensor updatedAirSensor = airSensor.get();
    updatedAirSensor.setId(Long.parseLong(newAirSensor.getId()));
    updatedAirSensor.setLocationName(newAirSensor.getLocationName());
    updatedAirSensor.setLocation(location.get());
    return airSensorRepository.save(updatedAirSensor);
  }
}
