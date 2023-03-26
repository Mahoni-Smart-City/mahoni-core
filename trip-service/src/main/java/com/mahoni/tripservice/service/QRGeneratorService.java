package com.mahoni.tripservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.mahoni.tripservice.dto.QRGeneratorRequest;
import com.mahoni.tripservice.dto.QRToken;
import com.mahoni.tripservice.exception.QRGeneratorNotFoundException;
import com.mahoni.tripservice.model.QRGenerator;
import com.mahoni.tripservice.repository.QRGeneratorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class QRGeneratorService {

  @Autowired
  QRGeneratorRepository qrGeneratorRepository;

  @Autowired
  CryptographyService cryptographyService;

  Integer QR_LIFESPAN = 5;

  @Value("${spring.security.qr-token.secret}")
  String secret;

  @Autowired
  private ObjectMapper objectMapper;

  static int MATRIX_WIDTH = 200;

  static int MATRIX_HEIGHT = 200;

  public static String generateQRCodeImage(String barcodeText) throws Exception {
    QRCodeWriter barcodeWriter = new QRCodeWriter();
    BitMatrix bitMatrix =
      barcodeWriter.encode(barcodeText, BarcodeFormat.QR_CODE, MATRIX_WIDTH, MATRIX_HEIGHT);

    BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

    ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
    MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
    return Base64Utils.encodeToString(pngOutputStream.toByteArray());
  }

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

  public String generateQRToken(UUID id) throws JsonProcessingException {
    Optional<QRGenerator> qrGenerator = qrGeneratorRepository.findById(id);
    if (qrGenerator.isEmpty()) {
      throw new QRGeneratorNotFoundException(id);
    }
    QRToken token = new QRToken(getNearestTokenStartAt(), getCurrentTokenExpiration(), id);
    String stringToken = objectMapper.writeValueAsString(token);
    return cryptographyService.encrypt(stringToken, secret);
  }

  public Boolean validateQRToken(String token) throws JsonProcessingException {
    String stringToken = cryptographyService.decrypt(token, secret);
    QRToken qrToken = objectMapper.readValue(stringToken, QRToken.class);
    return qrToken.getExpired_at().isAfter(LocalDateTime.now());
  }

  private LocalDateTime getNearestTokenStartAt() {
    LocalDateTime now = LocalDateTime.now();
    int minuteDiff = now.getMinute() % QR_LIFESPAN;
    return now.minusMinutes(minuteDiff).minusSeconds(now.getSecond()).minusNanos(now.getNano());
  }

  private LocalDateTime getCurrentTokenExpiration() {
    LocalDateTime now = LocalDateTime.now();
    int minuteDiff = now.getMinute() % QR_LIFESPAN;
    return now.minusMinutes(minuteDiff).plusMinutes(QR_LIFESPAN).minusSeconds(now.getSecond()).minusNanos(now.getNano());
  }
}
