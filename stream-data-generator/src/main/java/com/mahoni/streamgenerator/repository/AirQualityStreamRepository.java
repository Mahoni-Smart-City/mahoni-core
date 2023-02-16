package com.mahoni.streamgenerator.repository;

import com.mahoni.streamgenerator.model.AirQuality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AirQualityStreamRepository extends JpaRepository<AirQuality, Long> {

  @Query("SELECT a FROM AirQuality a WHERE a.timestamp = ?1 and a.sensorId = ?2")
  public AirQuality findByTimestampAndSensorId(Long timestamp, String sensorId);
}
