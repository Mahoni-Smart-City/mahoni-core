package com.mahoni.airqualityservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mahoni.airqualityservice.dto.AirQualityResponse;
import com.mahoni.airqualityservice.dto.AirSensorRequest;
import com.mahoni.airqualityservice.exception.AirSensorNotFoundException;
import com.mahoni.airqualityservice.model.AirSensor;
import com.mahoni.airqualityservice.model.Location;
import com.mahoni.airqualityservice.service.AirSensorService;
import com.mahoni.schema.AirQualityProcessedSchema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(AirSensorController.class)
public class AirSensorControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private AirSensorService airSensorService;

  AirSensor airSensor;

  AirSensorRequest request;

  @BeforeEach
  void init() {
    Long id = 1L;
    Location location = new Location();
    location.setId(id);
    airSensor = new AirSensor();
    airSensor.setId(id);
    airSensor.setNameLocation("Test");
    airSensor.setLocation(location);
    request = new AirSensorRequest(id, "Test", id);
  }

  @Test
  public void testPost_thenReturnNewAirSensor() throws Exception {
    when(airSensorService.create(any())).thenReturn(airSensor);

    MvcResult result = this.mockMvc.perform(post("/api/v1/air-sensors")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(airSensor));
    verify(airSensorService).create(any());
  }

  @Test
  public void testPost_thenThrowRuntimeException() throws Exception {
    when(airSensorService.create(any())).thenThrow(RuntimeException.class);

    this.mockMvc.perform(post("/api/v1/air-sensors")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isBadRequest())
      .andReturn();
  }

  @Test
  public void testGetAll_thenReturnAirSensors() throws Exception {
    List<AirSensor> airSensors = new ArrayList<>();

    when(airSensorService.getAll()).thenReturn(airSensors);

    MvcResult result = this.mockMvc.perform(get("/api/v1/air-sensors"))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(airSensors));
    verify(airSensorService).getAll();
  }

  @Test
  public void testGetById_thenReturnAirSensor() throws Exception {
    when(airSensorService.getById(any())).thenReturn(airSensor);

    MvcResult result = this.mockMvc.perform(get("/api/v1/air-sensors/{id}", 1L))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(airSensor));
    verify(airSensorService).getById(any());
  }

  @Test
  public void testGetById_thenThrowAirSensorNotFound() throws Exception {
    when(airSensorService.getById(any())).thenThrow(AirSensorNotFoundException.class);

    this.mockMvc.perform(get("/api/v1/air-sensors/{id}", 1L))
      .andExpect(status().isNotFound())
      .andReturn();
  }

  @Test
  public void testDelete_thenReturnAirSensor() throws Exception {
    when(airSensorService.deleteById(any())).thenReturn(airSensor);

    MvcResult result = this.mockMvc.perform(delete("/api/v1/air-sensors/{id}", 1L))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(airSensor));
    verify(airSensorService).deleteById(any());
  }

  @Test
  public void testDelete_thenThrowAirSensorNotFound() throws Exception {
    when(airSensorService.deleteById(any())).thenThrow(AirSensorNotFoundException.class);

    this.mockMvc.perform(delete("/api/v1/air-sensors/{id}", 1L))
      .andExpect(status().isNotFound())
      .andReturn();
  }

  @Test
  public void testUpdate_thenReturnAirSensor() throws Exception {
    when(airSensorService.update(any(), any())).thenReturn(airSensor);

    MvcResult result = this.mockMvc.perform(put("/api/v1/air-sensors/{id}", 1L)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(airSensor));
    verify(airSensorService).update(any(), any());
  }

  @Test
  public void testUpdate_thenThrowAirSensorNotFound() throws Exception {
    when(airSensorService.update(any(), any())).thenThrow(AirSensorNotFoundException.class);

    this.mockMvc.perform(put("/api/v1/air-sensors/{id}", 1L)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isNotFound())
      .andReturn();
  }

  @Test
  public void testGetAqi_thenReturnAirQuality() throws Exception {
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
    AirQualityResponse response = new AirQualityResponse(id, "TestId", timestamp, 1.0, 1.0, 1.0, 1.0);

    when(airSensorService.getAqi(any())).thenReturn(schema);

    MvcResult result = this.mockMvc.perform(get("/api/v1/air-sensors/{id}/aqi", id))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(response));
    verify(airSensorService).getAqi(any());
  }

  @Test
  public void testGetAqi_thenThrowAirSensorNotFound() throws Exception {
    when(airSensorService.getAqi(any())).thenThrow(AirSensorNotFoundException.class);

    this.mockMvc.perform(get("/api/v1/air-sensors/{id}/aqi", 1L))
      .andExpect(status().isNotFound())
      .andReturn();
  }
}
