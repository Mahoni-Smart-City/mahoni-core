package com.mahoni.tripservice.repository;

import com.mahoni.tripservice.model.QRGenerator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface QRGeneratorRepository extends JpaRepository<QRGenerator, UUID> {
}
