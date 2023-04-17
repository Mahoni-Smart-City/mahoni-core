package com.mahoni.tripservice.qrgenerator.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.mahoni.tripservice.qrgenerator.dto.QRGeneratorRequest;
import com.mahoni.tripservice.qrgenerator.dto.QRGeneratorType;
import com.mahoni.tripservice.qrgenerator.dto.QRToken;
import com.mahoni.tripservice.qrgenerator.exception.QRGeneratorNotFoundException;
import com.mahoni.tripservice.qrgenerator.model.QRGenerator;
import com.mahoni.tripservice.qrgenerator.model.QRGeneratorNode;
import com.mahoni.tripservice.qrgenerator.repository.QRGeneratorNodeRepository;
import com.mahoni.tripservice.qrgenerator.repository.QRGeneratorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.Base64Utils;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class QRGeneratorServiceTest {
  
  @Mock
  QRGeneratorRepository qrGeneratorRepository;

  @Mock
  QRGeneratorNodeRepository qrGeneratorNodeRepository;

  @Mock
  CryptographyService cryptographyService;

  @Mock
  ObjectMapper objectMapper;

  @InjectMocks
  QRGeneratorService qrGeneratorService;

  @Captor
  ArgumentCaptor<QRGenerator> qrGeneratorArgumentCaptor;

  @Test
  public void testGivenQrGeneratorRequest_thenSaveQrGenerator() {
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();
    QRGeneratorRequest request = new QRGeneratorRequest("Test", QRGeneratorType.HALTE, id1, id2);
    QRGenerator qrGenerator = new QRGenerator(UUID.randomUUID(), "Test", QRGeneratorType.HALTE.name(), id1, id2);

    when(qrGeneratorRepository.save(any())).thenReturn(qrGenerator);
    QRGenerator savedQrGenerator = qrGeneratorService.create(request);

    assertEquals(savedQrGenerator, qrGenerator);
    verify(qrGeneratorRepository).save(any());
  }

  @Test
  public void testGivenId_thenReturnQrGenerator() {
    UUID id = UUID.randomUUID();
    QRGenerator qrGenerator = new QRGenerator(id, "Test", QRGeneratorType.HALTE.name(), UUID.randomUUID(), UUID.randomUUID());

    when(qrGeneratorRepository.findById(any())).thenReturn(Optional.of(qrGenerator));
    QRGenerator savedQrGenerator = qrGeneratorService.getById(id);

    assertEquals(savedQrGenerator, qrGenerator);
    verify(qrGeneratorRepository).findById(any());
  }

  @Test
  public void testGivenId_thenThrowQrGeneratorNotFound() {
    UUID id = UUID.randomUUID();

    when(qrGeneratorRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(QRGeneratorNotFoundException.class, () -> qrGeneratorService.getById(id));
    assertThrows(QRGeneratorNotFoundException.class, () -> qrGeneratorService.generateQRToken(id));
  }

  @Test
  public void testGetAll_thenReturnQrGenerators() {
    QRGenerator qrGenerator = new QRGenerator(UUID.randomUUID(), "Test", QRGeneratorType.HALTE.name(), UUID.randomUUID(), UUID.randomUUID());
    List<QRGenerator> qrGenerators = new ArrayList<>();
    qrGenerators.add(qrGenerator);

    when(qrGeneratorRepository.findAll()).thenReturn(qrGenerators);
    List<QRGenerator> savedQrGenerators = qrGeneratorService.getAll();

    assertEquals(savedQrGenerators, qrGenerators);
    verify(qrGeneratorRepository).findAll();
  }

  @Test
  public void testGivenIdToBeDeleted_thenDeleteAndReturnDeletedQrGenerator() {
    UUID id = UUID.randomUUID();
    QRGenerator qrGenerator = new QRGenerator(UUID.randomUUID(), "Test", QRGeneratorType.HALTE.name(), UUID.randomUUID(), UUID.randomUUID());

    when(qrGeneratorRepository.findById(any())).thenReturn(Optional.of(qrGenerator));
    QRGenerator deletedQrGenerator = qrGeneratorService.deleteById(id);

    assertEquals(deletedQrGenerator, qrGenerator);
    verify(qrGeneratorRepository).deleteById(any());
  }

  @Test
  public void testGivenIdToBeDeleted_thenThrowQrGeneratorNotFound() {
    UUID id = UUID.randomUUID();

    when(qrGeneratorRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(QRGeneratorNotFoundException.class, () -> qrGeneratorService.deleteById(id));
  }

  @Test
  public void testGivenIdAndQrGeneratorRequest_thenUpdateAndReturnUpdatedQrGenerator() {
    UUID id = UUID.randomUUID();
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();
    QRGeneratorRequest request = new QRGeneratorRequest("Test", QRGeneratorType.HALTE, id1, id2);
    QRGenerator qrGenerator = new QRGenerator(UUID.randomUUID(), "Test", QRGeneratorType.HALTE.name(), UUID.randomUUID(), UUID.randomUUID());
    QRGenerator expectedQrGenerator = new QRGenerator(UUID.randomUUID(), "Test", QRGeneratorType.HALTE.name(), id1, id2);

    when(qrGeneratorRepository.findById(any())).thenReturn(Optional.of(qrGenerator));
    when(qrGeneratorRepository.save(any())).thenReturn(expectedQrGenerator);
    QRGenerator updatedQrGenerator = qrGeneratorService.updateById(id, request);

    assertEquals(updatedQrGenerator, expectedQrGenerator);
    verify(qrGeneratorRepository).save(qrGeneratorArgumentCaptor.capture());
    assertEquals(qrGeneratorArgumentCaptor.getValue().getSensorId1(), expectedQrGenerator.getSensorId1());
    assertEquals(qrGeneratorArgumentCaptor.getValue().getSensorId2(), expectedQrGenerator.getSensorId2());
  }

  @Test
  public void testGivenIdAndQrGeneratorRequest_thenThrowQrGeneratorNotFound() {
    UUID id = UUID.randomUUID();
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();
    QRGeneratorRequest request = new QRGeneratorRequest("Test", QRGeneratorType.HALTE, id1, id2);
    when(qrGeneratorRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(QRGeneratorNotFoundException.class, () -> qrGeneratorService.updateById(id, request));
  }

  @Test
  public void testGivenId_thenReturnQrToken() throws Exception {
    UUID id = UUID.randomUUID();
    QRGenerator qrGenerator = new QRGenerator(UUID.randomUUID(), "Test", QRGeneratorType.HALTE.name(), UUID.randomUUID(), UUID.randomUUID());
    String token = "Test";

    when(qrGeneratorRepository.findById(any())).thenReturn(Optional.of(qrGenerator));
    when(cryptographyService.encrypt(any(), any())).thenReturn(token);
    String expectedToken = qrGeneratorService.generateQRToken(id);

    assertEquals(expectedToken, token);
  }
  @Test
  public void testGivenTokenAndQrGeneratorId_thenReturnBoolean() throws Exception {
    UUID id = UUID.randomUUID();
    String token = "Test";
    QRToken expiredQrToken = new QRToken();
    expiredQrToken.setStartAt(LocalDateTime.now());
    expiredQrToken.setExpiredAt(LocalDateTime.now());
    expiredQrToken.setQRGeneratorId(id);
    QRToken notExpiredQrToken = new QRToken(
      expiredQrToken.getStartAt(),
      expiredQrToken.getExpiredAt().plusHours(1),
      id);

    when(cryptographyService.decrypt(any(), any())).thenReturn(token);

    when(objectMapper.readValue(token, QRToken.class)).thenReturn(expiredQrToken);
    assertFalse(qrGeneratorService.validateQRToken(token, id));

    when(objectMapper.readValue(token, QRToken.class)).thenReturn(notExpiredQrToken);
    assertTrue(qrGeneratorService.validateQRToken(token, id));
  }

  @Test
  public void testGetAllNode_thenReturnQrGeneratorNodes() {
    UUID id = UUID.randomUUID();
    QRGeneratorNode qrGeneratorNode = new QRGeneratorNode() ;
    qrGeneratorNode.setId(1L);
    qrGeneratorNode.setQrGeneratorId(id);
    qrGeneratorNode.setLocation("Test");
    qrGeneratorNode.setType(QRGeneratorType.HALTE.name());
    List<QRGeneratorNode> qrGeneratorNodes = new ArrayList<>();
    qrGeneratorNodes.add(qrGeneratorNode);

    when(qrGeneratorNodeRepository.findAll()).thenReturn(qrGeneratorNodes);
    List<QRGeneratorNode> savedQrGeneratorNodes = qrGeneratorService.getAllNode();

    assertEquals(savedQrGeneratorNodes, qrGeneratorNodes);
    verify(qrGeneratorNodeRepository).findAll();
  }

  @Test
  public void testGivenIdNode1AndIdNode2_thenReturnQrGeneratorNodes() {
    UUID id = UUID.randomUUID();
    QRGeneratorNode qrGeneratorNode1 = new QRGeneratorNode(1L, id, "Test", QRGeneratorType.HALTE.name());
    QRGeneratorNode qrGeneratorNode2 = new QRGeneratorNode(1L, id, "Test", QRGeneratorType.HALTE.name());
    List<QRGeneratorNode> qrGeneratorNodes = new ArrayList<>();
    qrGeneratorNodes.add(qrGeneratorNode1);
    qrGeneratorNodes.add(qrGeneratorNode2);

    when(qrGeneratorNodeRepository.shortestPath(any(), any())).thenReturn(qrGeneratorNodes);
    List<QRGeneratorNode> savedQrGeneratorNodes = qrGeneratorService.shortestPathBetweenNodes(id, id);

    assertEquals(savedQrGeneratorNodes, qrGeneratorNodes);
    verify(qrGeneratorNodeRepository).shortestPath(any(), any());
  }

  @Test
  public void testGivenBarcodeText_thenReturnString() throws Exception {
    String barcodeText = "Test";
    QRCodeWriter qrCodeWriter = new QRCodeWriter();
    BitMatrix bitMatrix = qrCodeWriter.encode(barcodeText, BarcodeFormat.QR_CODE, QRGeneratorService.MATRIX_WIDTH, QRGeneratorService.MATRIX_HEIGHT);

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    ImageIO.write(MatrixToImageWriter.toBufferedImage(bitMatrix), "PNG", byteArrayOutputStream);
    byte[] actualBytes = byteArrayOutputStream.toByteArray();

    String expected = QRGeneratorService.generateQRCodeImage(barcodeText);
    byte[] expectedBytes = Base64Utils.decodeFromString(expected);

    for (int i = 0; i < actualBytes.length; i++) {
      assertEquals(actualBytes[i], expectedBytes[i]);
    }
  }
}
