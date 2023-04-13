package com.mahoni.tripservice.trip.service;

import com.mahoni.tripservice.qrgenerator.exception.QRGeneratorNotFoundException;
import com.mahoni.tripservice.qrgenerator.model.QRGenerator;
import com.mahoni.tripservice.qrgenerator.model.QRGeneratorNode;
import com.mahoni.tripservice.qrgenerator.repository.QRGeneratorRepository;
import com.mahoni.tripservice.qrgenerator.service.QRGeneratorService;
import com.mahoni.tripservice.trip.dto.TripRequest;
import com.mahoni.tripservice.trip.model.TransactionStatus;
import com.mahoni.tripservice.trip.model.TripStatus;
import com.mahoni.tripservice.trip.kafka.TripEventProducer;
import com.mahoni.tripservice.trip.model.Trip;
import com.mahoni.tripservice.trip.repository.TripRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KafkaStreams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.lang.Math.max;

@Service
@Slf4j
public class TripService {

  @Autowired
  TripRepository tripRepository;

  @Autowired
  QRGeneratorRepository qrGeneratorRepository;

  @Autowired
  QRGeneratorService qrGeneratorService;

  @Autowired
  TripEventProducer tripEventProducer;

  @Autowired
  StreamsBuilderFactoryBean factoryBean;

  KafkaStreams kafkaStreams;

  @Value("${spring.trip.expired-duration}")
  Integer EXPIRED_DURATION;

  @Value("${spring.trip.base-multiplier}")
  Double BASE_MULTIPLIER;

  @Value("${spring.trip.aqi-multiplier}")
  Double AQI_MULTIPLIER;

  @Value("${spring.trip.duration-multiplier}")
  Double DURATION_MULTIPLIER;

  public List<Trip> getAllByUserId(UUID userId) throws Exception {
    Optional<List<Trip>> trips = tripRepository.findByUserId(userId);
    return trips.orElseGet(ArrayList::new);
  }

  @Transactional
  public Trip scanTrip(TripRequest tripRequest) throws Exception {
    Optional<Trip> latestTrip = tripRepository.findLatestActiveTripByUserId(tripRequest.getUserId());
    Optional<QRGenerator> qrGenerator = qrGeneratorRepository.findById(tripRequest.getScanPlaceId());
    if (qrGenerator.isEmpty()) {
      throw new QRGeneratorNotFoundException();
    }
    if (latestTrip.isEmpty()) {
      Trip newTrip = new Trip(tripRequest.getUserId(), qrGenerator.get(), LocalDateTime.now(), TripStatus.ACTIVE.name());
      tripEventProducer.send(newTrip);
      return tripRepository.save(newTrip);
    }

    Trip trip = latestTrip.get();
    if (isOngoing(trip)) {
      // scan out ongoing trip
      trip.setStatus(TripStatus.FINISHED.name());
      trip.setScanOutPlaceId(qrGenerator.get());
      trip.setScanOutAt(LocalDateTime.now());
      trip.setTransactionStatus(TransactionStatus.PENDING.name());

      // calculate point
      QRGenerator scanInPlace = trip.getScanInPlaceId();
      QRGenerator scanOutPlace = trip.getScanOutPlaceId();
      List<QRGeneratorNode> shortestPath = qrGeneratorService.shortestPathBetweenNodes(scanInPlace.getId(), scanOutPlace.getId());
      double aqi = maxAqi(shortestPath);
      int point = calculatePoint(aqi, trip);
      trip.setAqi(aqi);
      trip.setPoint(point);

      // send event to kafka
      tripEventProducer.send(trip);
      return tripRepository.save(trip);
    } else {
      // update expired trip and start new trip
      checkAndUpdateStatus(trip);
      Trip newTrip = new Trip(tripRequest.getUserId(), qrGenerator.get(), LocalDateTime.now(), TripStatus.ACTIVE.name());
      tripEventProducer.send(newTrip);
      return tripRepository.save(newTrip);
    }
  }

  public Trip getLatestTripByUserId(UUID userId) {
    Optional<Trip> trip = tripRepository.findLatestTripByUserId(userId);
    return trip.orElse(null);
  }

  @Scheduled(cron = "0 */6 * * * *")
  public void scheduleCheckAndUpdateStatus() {
    Optional<List<Trip>> ongoingTrips = tripRepository.findByStatus(TripStatus.ACTIVE.name());
    if (ongoingTrips.isPresent()) {
      for (Trip trip: ongoingTrips.get()) {
        checkAndUpdateStatus(trip);
      }
    }
  }

  public void checkAndUpdateStatus(Trip trip) {
    if (!isOngoing(trip) && trip.getStatus().equals(TripStatus.ACTIVE.name())) {
      trip.setStatus(TripStatus.EXPIRED.name());
      tripEventProducer.send(trip);
      tripRepository.save(trip);
    }
  }

  private int calculatePoint(double aqi, Trip trip) {
    Long durationInMinutes = Duration.between(trip.getScanInAt(), trip.getScanOutAt()).toMinutes();
    return (int) ((aqi * AQI_MULTIPLIER) + (durationInMinutes * DURATION_MULTIPLIER) * BASE_MULTIPLIER);
  }

  private double maxAqi(List<QRGeneratorNode> nodes) {
    // TODO: Update when can get value from KTable
    double aqi = 0;
    for (QRGeneratorNode node: nodes) {
      Optional<QRGenerator> qrGenerator = qrGeneratorRepository.findById(node.getQrGeneratorId());
    //    int aqi1 = getAqi(qrGenerator.getSensorId1()); // get from KTable
    //    int aqi2 = Integer.MIN_VALUE;
    //    if (qrGenerator.getSensorId2() != null) {
    //      aqi2 = getAqi(qrGenerator.getSensorId2()); // get from KTable
    //    }
    //    int maxAqi = max(aqi1, aqi2);
      double maxAqi = Math.abs(Math.random());
      if (aqi <= maxAqi) {
        aqi = maxAqi;
      }
    }
    return aqi;
  }

  private boolean isOngoing(Trip trip) {
    LocalDateTime now = LocalDateTime.now();
    return trip.getScanInAt().plusMinutes(EXPIRED_DURATION).isAfter(now);
  }
}
