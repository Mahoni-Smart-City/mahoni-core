package com.mahoni.tripservice.trip.controller;

import com.mahoni.tripservice.qrgenerator.exception.QRGeneratorNotFoundException;
import com.mahoni.tripservice.qrgenerator.service.QRGeneratorService;
import com.mahoni.tripservice.trip.dto.TripRequest;
import com.mahoni.tripservice.trip.model.Trip;
import com.mahoni.tripservice.trip.service.TripService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

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
      if (qrGeneratorService.validateQRToken(request.getQrToken(), request.getScanPlaceId())) {
        return ResponseEntity.ok(tripService.scanTrip(request));
      } else {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "QR Code is not valid");
      }
    } catch (ResponseStatusException e) {
      throw e;
    } catch (QRGeneratorNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  @GetMapping("/latest-trip/{userId}")
  public ResponseEntity<Trip> getLatestTrip(@PathVariable("userId") UUID userId) {
    return ResponseEntity.ok(tripService.getLatestTripByUserId(userId));
  }
}
