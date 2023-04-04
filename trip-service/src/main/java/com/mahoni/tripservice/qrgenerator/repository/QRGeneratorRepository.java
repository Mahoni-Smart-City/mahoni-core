package com.mahoni.tripservice.qrgenerator.repository;

import com.mahoni.tripservice.qrgenerator.model.QRGenerator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface QRGeneratorRepository extends JpaRepository<QRGenerator, UUID> {
}
