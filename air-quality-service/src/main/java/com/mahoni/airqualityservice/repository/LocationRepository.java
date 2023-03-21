package com.mahoni.airqualityservice.repository;

import com.mahoni.airqualityservice.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
