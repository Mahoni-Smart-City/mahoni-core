package com.mahoni.airqualityservice.repository;

import com.mahoni.airqualityservice.model.AirSensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AirSensorRepository extends JpaRepository<AirSensor, Long> {

  @Query("SELECT a FROM AirSensor a WHERE " +
    "LOWER(a.locationName) LIKE %?1% OR " +
    "LOWER(a.location.district) LIKE %?1% OR " +
    "LOWER(a.location.subDistrict) LIKE %?1% OR " +
    "LOWER(a.location.village) LIKE %?1%")
  List<AirSensor> findAirSensorsByLocation(String loc);
}
