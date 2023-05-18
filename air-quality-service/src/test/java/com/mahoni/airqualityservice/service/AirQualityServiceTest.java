package com.mahoni.airqualityservice.service;

import com.mahoni.airqualityservice.kafka.AirQualityServiceStream;
import com.mahoni.airqualityservice.model.AirSensor;
import com.mahoni.airqualityservice.model.Location;
import com.mahoni.airqualityservice.repository.AirSensorRepository;
import com.mahoni.flink.schema.AirQualityProcessedSchema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AirQualityServiceTest {

  @Mock
  AirSensorRepository airSensorRepository;

  @Mock
  AirQualityServiceStream airQualityServiceStream;

  @InjectMocks
  AirQualityService airQualityService;

  private AirQualityProcessedSchema schema;

  @BeforeEach
  public void init() {
    Long id = 1L;
    LocalDateTime datetime = LocalDateTime.now();
    LocalDateTime rounded = datetime.minusMinutes(datetime.getMinute()).minusSeconds(datetime.getSecond());
    Long timestamp = rounded.toEpochSecond(ZoneId.systemDefault().getRules().getOffset(rounded)) * 1000L;

    schema = AirQualityProcessedSchema.newBuilder()
      .setEventId(id.toString())
      .setSensorId(1L)
      .setTimestamp(timestamp)
      .setAqi(1.0)
      .setCategory("Good")
      .setCo(1.0)
      .setNo(1.0)
      .setNo2(1.0)
      .setO3(1.0)
      .setSo2(1.0)
      .setPm25(1.0)
      .setPm10(1.0)
      .setPm1(1.0)
      .setNh3(1.0)
      .setPressure(1.0)
      .setHumidity(1.0)
      .setTemperature(1.0)
      .setNameLocation("Test")
      .setIdLocation(1L)
      .setSubdistrict("Test")
      .setDistrict("Test")
      .build();
  }

  @Test
  public void testGivenSensorId_thenReturnAirQualityProcessed() {
    when(airQualityServiceStream.get(any())).thenReturn(schema);
    AirQualityProcessedSchema result = airQualityService.getAqi(1L);

    assertEquals(schema, result);
  }

  @Test
  public void testGivenLocation_thenReturnAirQualityProcessed() {
    List<AirSensor> airSensors = new ArrayList<>();
    airSensors.add(new AirSensor(1L, "Test", new Location()));
    List<AirQualityProcessedSchema> schemas = new ArrayList<>();
    schemas.add(schema);

    when(airSensorRepository.findAirSensorsByLocation(any())).thenReturn(airSensors);
    when(airQualityServiceStream.get(any())).thenReturn(schema);
    List<AirQualityProcessedSchema> results = airQualityService.getAqiByLocation("Test");

    assertEquals(schemas, results);
  }

  @Test
  public void testGivenCoordinate_thenReturnAirQualityProcessed() {
    List<AirSensor> airSensors = new ArrayList<>();
    Location location1 = new Location(1L, "Test", "Test", "Test", -10.00, 10.00);
    Location location2 = new Location(2L, "Test", "Test", "Test", 5.00, -5.00);
    airSensors.add(new AirSensor(1L, "Test", location1));
    airSensors.add(new AirSensor(2L, "Test", location2));

    when(airSensorRepository.findAll()).thenReturn(airSensors);
    when(airQualityServiceStream.get(any())).thenReturn(schema);
    AirQualityProcessedSchema result = airQualityService.getAqiByCoordinate(0.0, 0.0);

    assertEquals(schema, result);
    verify(airQualityServiceStream).get(AirQualityServiceStream.parseTableKey(2L));
  }

  @Test
  public void testGivenSensorId_thenReturnHistory() {
    when(airQualityServiceStream.get(any())).thenReturn(schema);
    Map<String, AirQualityProcessedSchema> result = airQualityService.history(1L);

    assertEquals(24*7, result.size());
  }
}
