package com.mahoni.tripservice.trip.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mahoni.tripservice.qrgenerator.exception.QRGeneratorNotFoundException;
import com.mahoni.tripservice.qrgenerator.service.QRGeneratorService;
import com.mahoni.tripservice.trip.dto.TripRequest;
import com.mahoni.tripservice.trip.model.Trip;
import com.mahoni.tripservice.trip.service.TripService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(TripController.class)
public class TripControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private TripService tripService;

  @MockBean
  private QRGeneratorService qrGeneratorService;

  @Test
  public void testPost_thenReturnTrip() throws Exception {
    Trip trip = new Trip();
    TripRequest request = new TripRequest("Token", UUID.randomUUID(), UUID.randomUUID());

    when(qrGeneratorService.validateQRToken(any(), any())).thenReturn(Boolean.TRUE);
    when(tripService.scanTrip(any())).thenReturn(trip);

    MvcResult result = this.mockMvc.perform(post("/api/v1/trips")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(trip));
    verify(tripService).scanTrip(any());
  }

  @Test
  public void testPost_thenThrowBadRequest() throws Exception {
    TripRequest request = new TripRequest("Token", UUID.randomUUID(), UUID.randomUUID());

    when(qrGeneratorService.validateQRToken(any(), any())).thenReturn(Boolean.FALSE);

    this.mockMvc.perform(post("/api/v1/trips")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isBadRequest())
      .andReturn();
  }

  @Test
  public void testPost_thenThrowNotFound() throws Exception {
    TripRequest request = new TripRequest("Token", UUID.randomUUID(), UUID.randomUUID());

    when(qrGeneratorService.validateQRToken(any(), any())).thenThrow(QRGeneratorNotFoundException.class);

    this.mockMvc.perform(post("/api/v1/trips")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isNotFound())
      .andReturn();
  }

  @Test
  public void testPost_thenThrowInternalServerError() throws Exception {
    TripRequest request = new TripRequest("Token", UUID.randomUUID(), UUID.randomUUID());

    when(qrGeneratorService.validateQRToken(any(), any())).thenThrow(JsonProcessingException.class);

    this.mockMvc.perform(post("/api/v1/trips")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isInternalServerError())
      .andReturn();
  }

  @Test
  public void testGetHistoryByUserId_thenReturnTrips() throws Exception {
    List<Trip> trips = new ArrayList<>();

    when(tripService.getAllByUserId(any())).thenReturn(trips);

    MvcResult result = this.mockMvc.perform(get("/api/v1/trips/history/{userId}", UUID.randomUUID()))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(trips));
    verify(tripService).getAllByUserId(any());
  }

  @Test
  public void testGetLatestTrip_thenReturnTrip() throws Exception {
    Trip trip = new Trip();

    when(tripService.getLatestTripByUserId(any())).thenReturn(trip);

    MvcResult result = this.mockMvc.perform(get("/api/v1/trips/latest-trip/{userId}", UUID.randomUUID()))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(trip));
    verify(tripService).getLatestTripByUserId(any());
  }
}
