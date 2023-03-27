package com.mahoni.airqualityservice.repository;

import com.mahoni.airqualityservice.model.AirSensor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AirSensorRepository extends JpaRepository<AirSensor, Long> {
}
