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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
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
    LocalDateTime datetime = LocalDateTime.now();
    LocalDateTime rounded = datetime.minusMinutes(datetime.getMinute()).minusSeconds(datetime.getSecond());
    Long timestamp = rounded.toEpochSecond(ZoneId.systemDefault().getRules().getOffset(rounded)) * 1000L;
    return airQualityServiceStream.get(timestamp + ":" + sensorId.toString());
  }

  public List<AirQualityProcessedSchema> getAqiByLocation(String location) {
    Optional<List<AirSensor>> airSensors = airSensorRepository.findAirSensorsByLocation(location);
    if (airSensors.isEmpty()) {
      throw new AirSensorNotFoundException();
    }
    return airSensors.get().stream().map(AirSensor::getId).map(this::getAqi).collect(Collectors.toList());
  }
}
