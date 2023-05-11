package com.mahoni.tripservice.trip.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mahoni.tripservice.qrgenerator.exception.QRGeneratorNotFoundException;
import com.mahoni.tripservice.qrgenerator.model.QRGenerator;
import com.mahoni.tripservice.qrgenerator.model.QRGeneratorNode;
import com.mahoni.tripservice.qrgenerator.model.QRGeneratorType;
import com.mahoni.tripservice.qrgenerator.repository.QRGeneratorRepository;
import com.mahoni.tripservice.qrgenerator.service.QRGeneratorService;
import com.mahoni.tripservice.trip.dto.TripRequest;
import com.mahoni.tripservice.trip.kafka.TripEventProducer;
import com.mahoni.tripservice.trip.kafka.TripServiceStream;
import com.mahoni.tripservice.trip.model.TripStatus;
import com.mahoni.tripservice.trip.model.TransactionStatus;
import com.mahoni.tripservice.trip.model.Trip;
import com.mahoni.tripservice.trip.repository.TripRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class TripServiceTest {

  @InjectMocks
  TripService tripService;

  @Mock
  TripRepository tripRepository;

  @Mock
  QRGeneratorRepository qrGeneratorRepository;

  @Mock
  QRGeneratorService qrGeneratorService;

  @Mock
  TripEventProducer tripEventProducer;

  @Mock
  TripServiceStream tripServiceStream;

  @Captor
  ArgumentCaptor<Trip> tripArgumentCaptor;

  @BeforeEach
  void init() {
    ReflectionTestUtils.setField(tripService, "EXPIRED_DURATION", 360);
    ReflectionTestUtils.setField(tripService, "BASE_MULTIPLIER", 1.0);
    ReflectionTestUtils.setField(tripService, "AQI_MULTIPLIER", 2.0);
    ReflectionTestUtils.setField(tripService, "DURATION_MULTIPLIER", 1.0);
  }

  @Test
  public void testGetAllByUserId_thenReturnTrips() {
    UUID id = UUID.randomUUID();
    Trip trip = new Trip();
    trip.setId(id);
    trip.setUserId(id);
    trip.setScanInPlaceId(new QRGenerator());
    trip.setScanOutPlaceId(new QRGenerator());
    trip.setScanInAt(LocalDateTime.now());
    trip.setScanOutAt(LocalDateTime.now());
    trip.setStatus(TripStatus.ACTIVE);
    trip.setAqi(1.0);
    trip.setPoint(0);
    List<Trip> trips = new ArrayList<>();
    trips.add(trip);

    when(tripRepository.findByUserId(any())).thenReturn(Optional.of(trips));
    List<Trip> expectedTrips = tripService.getAllByUserId(id);

    assertEquals(expectedTrips, trips);
  }

  @Test
  public void testGetAllByUserId_thenReturnEmptyList() {
    UUID id = UUID.randomUUID();
    List<Trip> trips = new ArrayList<>();

    when(tripRepository.findByUserId(any())).thenReturn(Optional.empty());
    List<Trip> expectedTrips = tripService.getAllByUserId(id);

    assertEquals(expectedTrips, trips);
  }

  @Test
  public void testScanIn_thenReturnTrip() {
    UUID id = UUID.randomUUID();
    QRGenerator qrGenerator = new QRGenerator("Test", QRGeneratorType.MRT, id, id);
    LocalDateTime time = LocalDateTime.now().minusDays(1);
    Trip trip = new Trip(id, id, qrGenerator, qrGenerator, time, time, TripStatus.ACTIVE, 1.0, 0, TransactionStatus.PENDING);
    TripRequest tripRequest = new TripRequest("Test", id, id);

    when(tripRepository.findLatestActiveTripByUserId(any())).thenReturn(Optional.of(trip));
    when(qrGeneratorRepository.findById(any())).thenReturn(Optional.of(qrGenerator));
    when(tripRepository.save(any())).thenReturn(trip);
    Trip expectedTrip = tripService.scanTrip(tripRequest);

    assertEquals(expectedTrip, trip);
  }

  @Test
  public void testScanOut_thenReturnTrip() {
    UUID id = UUID.randomUUID();
    QRGenerator qrGenerator = new QRGenerator("Test", QRGeneratorType.MRT, id, id);
    List<QRGeneratorNode> qrGeneratorNodes = new ArrayList<>();
    qrGeneratorNodes.add(new QRGeneratorNode());
    LocalDateTime time = LocalDateTime.now();
    Trip trip = new Trip(id, qrGenerator, time, TripStatus.ACTIVE);
    Trip expectedTrip = new Trip(id, id, qrGenerator, qrGenerator, time, time, TripStatus.FINISHED, 1.0, 1, TransactionStatus.PENDING);
    TripRequest tripRequest = new TripRequest("Test", id, id);

    when(tripRepository.findLatestActiveTripByUserId(any())).thenReturn(Optional.of(trip));
    when(qrGeneratorService.shortestPathBetweenNodes(any(), any())).thenReturn(qrGeneratorNodes);
    when(qrGeneratorRepository.findById(any())).thenReturn(Optional.of(qrGenerator));
    when(tripRepository.save(any())).thenReturn(expectedTrip);
    Trip updatedTrip = tripService.scanTrip(tripRequest);

    assertEquals(updatedTrip, expectedTrip);
    verify(tripRepository).save(tripArgumentCaptor.capture());
    assertEquals(tripArgumentCaptor.getValue().getStatus(), expectedTrip.getStatus());
    assertEquals(tripArgumentCaptor.getValue().getScanOutPlaceId(), expectedTrip.getScanOutPlaceId());
  }

  @Test
  public void testScanTrip_thenReturnNewTrip() {
    Trip trip = new Trip(UUID.randomUUID(), new QRGenerator(), LocalDateTime.now(), TripStatus.ACTIVE);
    QRGenerator qrGenerator = new QRGenerator("Test", QRGeneratorType.MRT, UUID.randomUUID(), UUID.randomUUID());
    TripRequest tripRequest = new TripRequest("Test", UUID.randomUUID(), UUID.randomUUID());

    when(tripRepository.findLatestActiveTripByUserId(any())).thenReturn(Optional.empty());
    when(qrGeneratorRepository.findById(any())).thenReturn(Optional.of(qrGenerator));
    when(tripRepository.save(any())).thenReturn(trip);
    Trip expectedTrip = tripService.scanTrip(tripRequest);

    assertEquals(expectedTrip, trip);
  }

  @Test
  public void testScanTrip_thenThrowQRGeneratorNotFound() {
    Trip trip = new Trip(UUID.randomUUID(), new QRGenerator(), LocalDateTime.now(), TripStatus.ACTIVE);
    TripRequest tripRequest = new TripRequest("Test", UUID.randomUUID(), UUID.randomUUID());

    when(tripRepository.findLatestActiveTripByUserId(any())).thenReturn(Optional.of(trip));
    when(qrGeneratorRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(QRGeneratorNotFoundException.class, () -> tripService.scanTrip(tripRequest));
  }

  @Test
  public void testGetLatestTripByUserId_thenReturnTrip() {
    Trip trip = new Trip();

    when(tripRepository.findLatestTripByUserId(any())).thenReturn(Optional.of(trip));
    Trip expectedTrip = tripService.getLatestTripByUserId(UUID.randomUUID());

    assertEquals(expectedTrip, trip);
  }

  @Test
  public void testGetLatestTripByUserId_thenReturnNull() {
    when(tripRepository.findLatestTripByUserId(any())).thenReturn(Optional.empty());
    Trip expectedTrip = tripService.getLatestTripByUserId(UUID.randomUUID());

    assertNull(expectedTrip);
  }

  @Test
  public void testScheduleCheckAndUpdateStatus() throws Exception {
    Trip trip = new Trip(UUID.randomUUID(), new QRGenerator(), LocalDateTime.now().minusDays(1), TripStatus.ACTIVE);
    Trip expectedTrip = new Trip(UUID.randomUUID(), new QRGenerator(), LocalDateTime.now().minusDays(1), TripStatus.EXPIRED);
    List<Trip> trips = new ArrayList<>();
    List<Trip> expectedTrips = new ArrayList<>();
    ObjectMapper objectMapper = mock(ObjectMapper.class);
    trips.add(trip);
    expectedTrips.add(expectedTrip);

    when(tripRepository.findByStatus(any())).thenReturn(Optional.of(trips));
    tripService.scheduleCheckAndUpdateStatus();

    assertEquals(objectMapper.writeValueAsString(expectedTrips), objectMapper.writeValueAsString(trips));
  }
}
