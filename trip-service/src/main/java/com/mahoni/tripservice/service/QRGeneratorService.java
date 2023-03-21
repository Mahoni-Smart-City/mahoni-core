package com.mahoni.tripservice.service;

import com.mahoni.tripservice.dto.QRGeneratorRequest;
import com.mahoni.tripservice.exception.QRGeneratorNotFoundException;
import com.mahoni.tripservice.model.QRGenerator;
import com.mahoni.tripservice.repository.QRGeneratorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class QRGeneratorService {

  @Autowired
  QRGeneratorRepository qrGeneratorRepository;

  public QRGenerator create(QRGeneratorRequest qrGenerator) {
    return qrGeneratorRepository.save(new QRGenerator(
      qrGenerator.getLocation(),
      qrGenerator.getType().name(),
      qrGenerator.getSensorId1(),
      qrGenerator.getSensorId2())
    );
  }

  public QRGenerator getById(UUID id) {
    Optional<QRGenerator> qrGenerator = qrGeneratorRepository.findById(id);
    if (qrGenerator.isEmpty()) {
      throw new QRGeneratorNotFoundException(id);
    }
    return qrGenerator.get();
  }

  public List<QRGenerator> getAll() {
    return qrGeneratorRepository.findAll();
  }

  public QRGenerator deleteById(UUID id) {
    Optional<QRGenerator> qrGenerator = qrGeneratorRepository.findById(id);
    if (qrGenerator.isEmpty()) {
      throw new QRGeneratorNotFoundException(id);
    }
    qrGeneratorRepository.deleteById(id);
    return qrGenerator.get();
  }

  public QRGenerator updateById(UUID id, QRGeneratorRequest newQRGenerator) {
    Optional<QRGenerator> qrGenerator = qrGeneratorRepository.findById(id);
    if (qrGenerator.isEmpty()) {
      throw new QRGeneratorNotFoundException(id);
    }
    QRGenerator updatedQRGenerator = qrGenerator.get();
    updatedQRGenerator.setLocation(newQRGenerator.getLocation());
    updatedQRGenerator.setType(newQRGenerator.getType().name());
    updatedQRGenerator.setSensorId1(newQRGenerator.getSensorId1());
    updatedQRGenerator.setSensorId2(newQRGenerator.getSensorId2());
    return qrGeneratorRepository.save(updatedQRGenerator);
  }
}
