package com.mahoni.airqualityservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mahoni.airqualityservice.dto.AirQualityResponse;
import com.mahoni.airqualityservice.exception.AirSensorNotFoundException;
import com.mahoni.airqualityservice.service.AirQualityService;
import com.mahoni.schema.AirQualityProcessedSchema;
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
import java.util.ArrayList;
import java.util.List;

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

  @Test
  public void testGetAqiById_thenReturnAirQuality() throws Exception {
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
    AirQualityResponse response1 = new AirQualityResponse(id, "TestId", timestamp, 1.0, 1.0, 1.0, 1.0);
    AirQualityResponse response2 = new AirQualityResponse();
    response2.setEventId(id);
    response2.setSensorId("TestId");
    response2.setTimestamp(timestamp);
    response2.setAqi(1.0);
    response2.setNo2(1.0);
    response2.setPm25(1.0);
    response2.setPm10(1.0);

    when(airQualityService.getAqi(any())).thenReturn(schema);

    MvcResult result = this.mockMvc.perform(get("/api/v1/air-quality/id/{id}", id))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(response1));
    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(response2));
    verify(airQualityService).getAqi(any());
  }

  @Test
  public void testGetAqiById_thenThrowAirSensorNotFound() throws Exception {
    when(airQualityService.getAqi(any())).thenThrow(AirSensorNotFoundException.class);

    this.mockMvc.perform(get("/api/v1/air-quality/id/{id}", 1L))
      .andExpect(status().isNotFound())
      .andReturn();
  }

  @Test
  public void testGetAqiByLoc_thenReturnAirQuality() throws Exception {
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
    List<AirQualityProcessedSchema> schemas = new ArrayList<>();
    schemas.add(schema);
    List<AirQualityResponse> responses = new ArrayList<>();
    responses.add(new AirQualityResponse(id, "TestId", timestamp, 1.0, 1.0, 1.0, 1.0));

    when(airQualityService.getAqiByLocation(any())).thenReturn(schemas);

    MvcResult result = this.mockMvc.perform(get("/api/v1/air-quality/loc/{loc}", "test"))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(responses));
    verify(airQualityService).getAqiByLocation(any());
  }

  @Test
  public void testGetAqiByLoc_thenThrowAirSensorNotFound() throws Exception {
    when(airQualityService.getAqiByLocation(any())).thenThrow(AirSensorNotFoundException.class);

    this.mockMvc.perform(get("/api/v1/air-quality/loc/{loc}", "test"))
      .andExpect(status().isNotFound())
      .andReturn();
  }
}
