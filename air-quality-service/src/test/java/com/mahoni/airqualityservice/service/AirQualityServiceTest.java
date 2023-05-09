package com.mahoni.airqualityservice.service;

import com.mahoni.airqualityservice.exception.AirSensorNotFoundException;
import com.mahoni.airqualityservice.kafka.AirQualityServiceStream;
import com.mahoni.airqualityservice.model.AirSensor;
import com.mahoni.airqualityservice.model.Location;
import com.mahoni.airqualityservice.repository.AirSensorRepository;
import com.mahoni.schema.AirQualityProcessedSchema;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AirQualityServiceTest {

  @Mock
  AirSensorRepository airSensorRepository;

  @Mock
  AirQualityServiceStream airQualityServiceStream;

  @InjectMocks
  AirQualityService airQualityService;

  @Test
  public void testGivenSensorId_thenReturnAirQualityProcessed() {
    Long id = 1L;
    LocalDateTime datetime = LocalDateTime.now();
    LocalDateTime rounded = datetime.minusMinutes(datetime.getMinute()).minusSeconds(datetime.getSecond());
    Long timestamp = rounded.toEpochSecond(ZoneId.systemDefault().getRules().getOffset(rounded)) * 1000L;
    AirQualityProcessedSchema schema = AirQualityProcessedSchema.newBuilder()
      .setEventId(id.toString())
      .setSensorId("TestId")
      .setTimestamp(timestamp)
      .setAqi(1.0)
      .setNo2(1.0)
      .setPm10(1.0)
      .setPm25(1.0)
      .build();

    when(airQualityServiceStream.get(any())).thenReturn(schema);
    AirQualityProcessedSchema result = airQualityService.getAqi(id);

    assertEquals(schema, result);
  }

  @Test
  public void testGivenLocation_thenReturnAirQualityProcessed() {
    Long id = 1L;
    List<AirSensor> airSensors = new ArrayList<>();
    airSensors.add(new AirSensor(id, "Test", new Location()));
    LocalDateTime datetime = LocalDateTime.now();
    LocalDateTime rounded = datetime.minusMinutes(datetime.getMinute()).minusSeconds(datetime.getSecond());
    Long timestamp = rounded.toEpochSecond(ZoneId.systemDefault().getRules().getOffset(rounded)) * 1000L;
    AirQualityProcessedSchema schema = AirQualityProcessedSchema.newBuilder()
      .setEventId(id.toString())
      .setSensorId("TestId")
      .setTimestamp(timestamp)
      .setAqi(1.0)
      .setNo2(1.0)
      .setPm10(1.0)
      .setPm25(1.0)
      .build();
    List<AirQualityProcessedSchema> schemas = new ArrayList<>();
    schemas.add(schema);

    when(airSensorRepository.findAirSensorsByLocation(any())).thenReturn(Optional.of(airSensors));
    when(airQualityServiceStream.get(any())).thenReturn(schema);
    List<AirQualityProcessedSchema> results = airQualityService.getAqiByLocation("Test");

    assertEquals(schemas, results);
  }

  @Test
  public void testGivenLocation_thenThrowAirSensorNotFound() {
    when(airSensorRepository.findAirSensorsByLocation(any())).thenReturn(Optional.empty());

    assertThrows(AirSensorNotFoundException.class, () -> airQualityService.getAqiByLocation("Test"));
  }
}
