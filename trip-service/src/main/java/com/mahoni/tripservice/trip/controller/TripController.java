package com.mahoni.tripservice.trip.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mahoni.tripservice.qrgenerator.service.QRGeneratorService;
import com.mahoni.tripservice.trip.dto.TripRequest;
import com.mahoni.tripservice.trip.model.Trip;
import com.mahoni.tripservice.trip.service.TripService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/trips")
public class TripController {

  @Autowired
  QRGeneratorService qrGeneratorService;

  @Autowired
  TripService tripService;

  @PostMapping
  public ResponseEntity<Trip> post(@Valid @RequestBody TripRequest request) {
    try {
      if (qrGeneratorService.validateQRToken(request.getQrToken())) {
        return ResponseEntity.ok(tripService.scanTrip(request));
      } else {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "QR Code is not valid");
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
