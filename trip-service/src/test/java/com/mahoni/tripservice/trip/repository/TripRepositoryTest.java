package com.mahoni.tripservice.trip.repository;

import com.mahoni.tripservice.qrgenerator.model.QRGenerator;
import com.mahoni.tripservice.qrgenerator.model.QRGeneratorType;
import com.mahoni.tripservice.trip.model.Trip;
import com.mahoni.tripservice.trip.model.TripStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class TripRepositoryTest {

  @Autowired
  private TripRepository tripRepository;

  @Autowired
  private TestEntityManager testEntityManager;

  @Test
  public void testFIndByUserId() {
    UUID id = UUID.randomUUID();
    QRGenerator qrGenerator = new QRGenerator("Test", QRGeneratorType.COMMUTER, 1L);

    testEntityManager.persist(qrGenerator);
    testEntityManager.persist(new Trip(id, qrGenerator, LocalDateTime.now(), TripStatus.EXPIRED));
    testEntityManager.persist(new Trip(id, qrGenerator, LocalDateTime.now(), TripStatus.ACTIVE));
    testEntityManager.persist(new Trip(UUID.randomUUID(), qrGenerator, LocalDateTime.now(), TripStatus.FINISHED));

    Optional<List<Trip>> trips = tripRepository.findByUserId(id);

    assertFalse(trips.isEmpty());
    assertEquals(2, trips.get().size());
    assertEquals(id, trips.get().get(0).getUserId());
    assertEquals(id, trips.get().get(1).getUserId());
  }

  @Test
  public void testFIndByStatus() {
    UUID id = UUID.randomUUID();
    QRGenerator qrGenerator = new QRGenerator("Test", QRGeneratorType.COMMUTER, 1L);

    testEntityManager.persist(qrGenerator);
    testEntityManager.persist(new Trip(id, qrGenerator, LocalDateTime.now(), TripStatus.EXPIRED));
    testEntityManager.persist(new Trip(id, qrGenerator, LocalDateTime.now(), TripStatus.ACTIVE));
    testEntityManager.persist(new Trip(UUID.randomUUID(), qrGenerator, LocalDateTime.now(), TripStatus.FINISHED));

    Optional<List<Trip>> trips = tripRepository.findByStatus(TripStatus.ACTIVE);

    assertFalse(trips.isEmpty());
    assertEquals(1, trips.get().size());
    assertEquals(TripStatus.ACTIVE, trips.get().get(0).getStatus());
  }

  @Test
  public void testFindLatestTripByUserId() {
    UUID id = UUID.randomUUID();
    QRGenerator qrGenerator = new QRGenerator("Test", QRGeneratorType.COMMUTER, 1L);

    testEntityManager.persist(qrGenerator);
    testEntityManager.persist(new Trip(id, qrGenerator, LocalDateTime.now(), TripStatus.ACTIVE));
    testEntityManager.persist(new Trip(id, qrGenerator, LocalDateTime.now(), TripStatus.EXPIRED));

    Optional<Trip> trip = tripRepository.findLatestTripByUserId(id);

    assertTrue(trip.isPresent());
    assertEquals(id, trip.get().getUserId());
    assertEquals(TripStatus.EXPIRED, trip.get().getStatus());
  }

  @Test
  public void testFindLatestActiveTripByUserId() {
    UUID id = UUID.randomUUID();
    QRGenerator qrGenerator1 = new QRGenerator("Test1", QRGeneratorType.COMMUTER, 1L);
    QRGenerator qrGenerator2 = new QRGenerator("Test2", QRGeneratorType.COMMUTER, 1L);

    testEntityManager.persist(qrGenerator1);
    testEntityManager.persist(qrGenerator2);
    testEntityManager.persist(new Trip(id, qrGenerator1, LocalDateTime.now(), TripStatus.ACTIVE));
    testEntityManager.persist(new Trip(id, qrGenerator2, LocalDateTime.now(), TripStatus.ACTIVE));

    Optional<Trip> trip = tripRepository.findLatestActiveTripByUserId(id);

    assertTrue(trip.isPresent());
    assertEquals(TripStatus.ACTIVE, trip.get().getStatus());
    assertEquals("Test2", trip.get().getScanInPlaceId().getLocation());
  }
}
