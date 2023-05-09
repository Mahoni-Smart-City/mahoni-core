package com.mahoni.airqualityservice.repository;

import com.mahoni.airqualityservice.model.AirSensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AirSensorRepository extends JpaRepository<AirSensor, Long> {

  @Query("SELECT a FROM AirSensor a WHERE a.location.district LIKE %?1% OR a.location.subDistrict LIKE %?1% OR a.location.village LIKE %?1%")
  Optional<List<AirSensor>> findAirSensorsByLocation(String loc);
}
