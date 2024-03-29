package com.mahoni.tripservice.qrgenerator.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.mahoni.tripservice.qrgenerator.dto.QRGeneratorRequest;
import com.mahoni.tripservice.qrgenerator.dto.QRToken;
import com.mahoni.tripservice.qrgenerator.exception.QRGeneratorNotFoundException;
import com.mahoni.tripservice.qrgenerator.model.QRGenerator;
import com.mahoni.tripservice.qrgenerator.model.QRGeneratorNode;
import com.mahoni.tripservice.qrgenerator.repository.QRGeneratorNodeRepository;
import com.mahoni.tripservice.qrgenerator.repository.QRGeneratorRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class QRGeneratorService {

  @Autowired
  QRGeneratorRepository qrGeneratorRepository;

  @Autowired
  QRGeneratorNodeRepository qrGeneratorNodeRepository;

  @Autowired
  CryptographyService cryptographyService;

  @Value("${spring.security.qr-token.lifespan}")
  Integer QR_LIFESPAN = 5;

  @Value("${spring.security.qr-token.secret}")
  String secret;

  @Autowired
  ObjectMapper objectMapper;

  private Map<String, Map<String, String>> cachedToken = new HashMap<>();

  static int MATRIX_WIDTH = 200;

  static int MATRIX_HEIGHT = 200;

  public static String generateQRCodeImage(String qrText) throws Exception {
    QRCodeWriter qrWriter = new QRCodeWriter();
    BitMatrix bitMatrix =
      qrWriter.encode(qrText, BarcodeFormat.QR_CODE, MATRIX_WIDTH, MATRIX_HEIGHT);

    BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

    ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
    MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
    return Base64Utils.encodeToString(pngOutputStream.toByteArray());
  }

  public static String decodeQRCodeImage(String qrCodeText) throws Exception {
    byte[] decoded = Base64.decodeBase64(qrCodeText);
    BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(decoded));
    LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
    return new MultiFormatReader().decode(bitmap).getText();
  }

  public QRGenerator create(QRGeneratorRequest qrGenerator) {
    return qrGeneratorRepository.save(new QRGenerator(
      qrGenerator.getLocation(),
      qrGenerator.getType(),
      Long.parseLong(qrGenerator.getSensorId1()),
      Long.parseLong(qrGenerator.getSensorId2())
    ));
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
    updatedQRGenerator.setType(newQRGenerator.getType());
    updatedQRGenerator.setSensorId1(Long.parseLong(newQRGenerator.getSensorId1()));
    updatedQRGenerator.setSensorId2(Long.parseLong(newQRGenerator.getSensorId2()));
    return qrGeneratorRepository.save(updatedQRGenerator);
  }

  public String generateQRToken(UUID id) throws JsonProcessingException {
    Optional<QRGenerator> qrGenerator = qrGeneratorRepository.findById(id);
    if (qrGenerator.isEmpty()) {
      throw new QRGeneratorNotFoundException(id);
    }
    QRToken token = new QRToken(nearestTokenStartAt(), currentTokenExpiration(), id);
    Map<String, String> cached = cachedToken.get(nearestTokenStartAt().toString());
    if (cached != null) {
      String cachedTokenString = cached.get(token.getQRGeneratorId().toString());
      if (cachedTokenString != null) {
        log.info("Use cached string token for id=" + token.getQRGeneratorId().toString() + " and startTime=" + nearestTokenStartAt());
        return cachedTokenString;
      }
    }
    cachedToken.clear();
    String stringToken = objectMapper.writeValueAsString(token);
    String newStringToken = cryptographyService.encrypt(stringToken, secret);
    cached = new HashMap<>();
    cached.put(token.getQRGeneratorId().toString(), newStringToken);
    cachedToken.put(nearestTokenStartAt().toString(), cached);
    return newStringToken;
  }

  public Boolean validateQRToken(String token, UUID qrGeneratorId) throws JsonProcessingException {
    String stringToken = cryptographyService.decrypt(token, secret);
    QRToken qrToken = objectMapper.readValue(stringToken, QRToken.class);
    return qrToken.getExpiredAt().isAfter(LocalDateTime.now()) && qrGeneratorId.equals(qrToken.getQRGeneratorId());
  }

  public List<QRGeneratorNode> getAllNode() {
    List<QRGeneratorNode> nodes = qrGeneratorNodeRepository.findAll();
    for (QRGeneratorNode node: nodes) {
      System.out.println(node);
    }
    return nodes;
  }

  public List<QRGeneratorNode> shortestPathBetweenNodes(UUID node1, UUID node2) {
    return qrGeneratorNodeRepository.shortestPath(node1, node2);
  }

  private LocalDateTime nearestTokenStartAt() {
    LocalDateTime now = LocalDateTime.now();
    int minuteDiff = now.getMinute() % QR_LIFESPAN;
    return now.minusMinutes(minuteDiff).minusSeconds(now.getSecond()).minusNanos(now.getNano());
  }

  private LocalDateTime currentTokenExpiration() {
    LocalDateTime now = LocalDateTime.now();
    int minuteDiff = now.getMinute() % QR_LIFESPAN;
    return now.minusMinutes(minuteDiff).plusMinutes(QR_LIFESPAN).minusSeconds(now.getSecond()).minusNanos(now.getNano());
  }
}
