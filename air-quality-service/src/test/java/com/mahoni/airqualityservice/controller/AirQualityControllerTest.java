package com.mahoni.airqualityservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mahoni.airqualityservice.dto.AirQualityResponse;
import com.mahoni.airqualityservice.exception.AirSensorNotFoundException;
import com.mahoni.airqualityservice.model.Location;
import com.mahoni.airqualityservice.repository.LocationRepository;
import com.mahoni.airqualityservice.service.AirQualityService;
import com.mahoni.schema.AirQualityProcessedSchema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(AirQualityController.class)
public class AirQualityControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private AirQualityService airQualityService;

  @MockBean
  private LocationRepository locationRepository;

  private AirQualityProcessedSchema schema;

  private AirQualityResponse response;

  @BeforeEach
  void init() {
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

    response = new AirQualityResponse();
    response.setSensorId(1L);
    response.setTimestamp(timestamp);
    response.setAqi(1.0);
    response.setCategory("Good");
    response.setCo(1.0);
    response.setNo(1.0);
    response.setNo2(1.0);
    response.setO3(1.0);
    response.setSo2(1.0);
    response.setPm25(1.0);
    response.setPm10(1.0);
    response.setNh3(1.0);
    response.setPressure(1.0);
    response.setHumidity(1.0);
    response.setTemperature(1.0);
    response.setLocation(new Location());
    response.setRecommendation(
      AirQualityController.Recommendation.recommendation.get("Good")
    );
  }
  @Test
  public void testGetAqiById_thenReturnAirQualityResponse() throws Exception {
    when(airQualityService.getAqi(any())).thenReturn(schema);
    when(locationRepository.findById(any())).thenReturn(Optional.of(new Location()));

    MvcResult result = this.mockMvc.perform(get("/api/v1/air-quality/id/{id}", 1L))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(response));
    verify(airQualityService).getAqi(any());
  }

  @Test
  public void testGet_thenThrowAirSensorNotFound() throws Exception {
    when(airQualityService.getAqi(any())).thenThrow(AirSensorNotFoundException.class);

    this.mockMvc.perform(get("/api/v1/air-quality/id/{id}", 1L))
      .andExpect(status().isNotFound())
      .andReturn();
  }

  @Test
  public void testGetByLoc_thenReturnAirQualityResponse() throws Exception {
    List<AirQualityProcessedSchema> schemas = new ArrayList<>();
    schemas.add(schema);
    List<AirQualityResponse> responses = new ArrayList<>();
    responses.add(response);

    when(airQualityService.getAqiByLocation(any())).thenReturn(schemas);

    MvcResult result = this.mockMvc.perform(get("/api/v1/air-quality/loc/{loc}", "test"))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(responses));
    verify(airQualityService).getAqiByLocation(any());
  }

  @Test
  public void testGetByLoc_thenThrowAirSensorNotFound() throws Exception {
    when(airQualityService.getAqiByLocation(any())).thenThrow(AirSensorNotFoundException.class);

    this.mockMvc.perform(get("/api/v1/air-quality/loc/{loc}", "test"))
      .andExpect(status().isNotFound())
      .andReturn();
  }

  @Test
  public void testHistory_thenReturnMap() throws Exception {
    String key = LocalDateTime.now().getDayOfWeek().name() + ":" + LocalDateTime.now().getHour() + ":" + 1L;
    HashMap<String, AirQualityProcessedSchema> history = new HashMap<>();
    history.put(key, schema);
    Map<String, AirQualityResponse> mapResponse = Map.of(key, response);

    when(airQualityService.history(any())).thenReturn(history);

    MvcResult result = this.mockMvc.perform(get("/api/v1/air-quality/history/{id}", 1L))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(mapResponse));
    verify(airQualityService).history(any());
  }
}
