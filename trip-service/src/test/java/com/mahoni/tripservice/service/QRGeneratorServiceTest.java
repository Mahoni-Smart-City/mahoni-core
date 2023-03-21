package com.mahoni.tripservice.service;

import com.mahoni.tripservice.dto.QRGeneratorRequest;
import com.mahoni.tripservice.dto.QRGeneratorType;
import com.mahoni.tripservice.exception.QRGeneratorNotFoundException;
import com.mahoni.tripservice.model.QRGenerator;
import com.mahoni.tripservice.repository.QRGeneratorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class QRGeneratorServiceTest {
  
  @Mock
  QRGeneratorRepository qrGeneratorRepository;

  @InjectMocks
  QRGeneratorService qrGeneratorService;

  @Captor
  ArgumentCaptor<QRGenerator> qrGeneratorArgumentCaptor;

  @Test
  public void testGivenQrGeneratorRequest_thenSaveQrGenerator() throws Exception {
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
  public void testGivenId_thenReturnQrGenerator() throws Exception {
    UUID id = UUID.randomUUID();
    QRGenerator qrGenerator = new QRGenerator(id, "Test", QRGeneratorType.HALTE.name(), UUID.randomUUID(), UUID.randomUUID());

    when(qrGeneratorRepository.findById(any())).thenReturn(Optional.of(qrGenerator));
    QRGenerator savedQrGenerator = qrGeneratorService.getById(id);

    assertEquals(savedQrGenerator, qrGenerator);
    verify(qrGeneratorRepository).findById(any());
  }

  @Test
  public void testGivenId_thenThrowQrGeneratorNotFound() throws Exception {
    UUID id = UUID.randomUUID();

    when(qrGeneratorRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(QRGeneratorNotFoundException.class, () -> {
      qrGeneratorService.getById(id);
    });
  }

  @Test
  public void testGetAll_thenReturnQrGenerators() throws Exception {
    QRGenerator qrGenerator = new QRGenerator(UUID.randomUUID(), "Test", QRGeneratorType.HALTE.name(), UUID.randomUUID(), UUID.randomUUID());
    List<QRGenerator> qrGenerators = new ArrayList<>();
    qrGenerators.add(qrGenerator);

    when(qrGeneratorRepository.findAll()).thenReturn(qrGenerators);
    List<QRGenerator> savedQrGenerators = qrGeneratorService.getAll();

    assertEquals(savedQrGenerators, qrGenerators);
    verify(qrGeneratorRepository).findAll();
  }

  @Test
  public void testGivenIdToBeDeleted_thenDeleteAndReturnDeletedQrGenerator() throws Exception {
    UUID id = UUID.randomUUID();
    QRGenerator qrGenerator = new QRGenerator(UUID.randomUUID(), "Test", QRGeneratorType.HALTE.name(), UUID.randomUUID(), UUID.randomUUID());

    when(qrGeneratorRepository.findById(any())).thenReturn(Optional.of(qrGenerator));
    QRGenerator deletedQrGenerator = qrGeneratorService.deleteById(id);

    assertEquals(deletedQrGenerator, qrGenerator);
    verify(qrGeneratorRepository).deleteById(any());
  }

  @Test
  public void testGivenIdToBeDeleted_thenThrowQrGeneratorNotFound() throws Exception {
    UUID id = UUID.randomUUID();

    when(qrGeneratorRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(QRGeneratorNotFoundException.class, () -> {
      qrGeneratorService.deleteById(id);
    });
  }

  @Test
  public void testGivenIdAndQrGeneratorRequest_thenUpdateAndReturnUpdatedQrGenerator() throws Exception {
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
  public void testGivenIdAndQrGeneratorRequest_thenThrowQrGeneratorNotFound() throws Exception {
    UUID id = UUID.randomUUID();
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();
    QRGeneratorRequest request = new QRGeneratorRequest("Test", QRGeneratorType.HALTE, id1, id2);
    when(qrGeneratorRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(QRGeneratorNotFoundException.class, () -> {
      qrGeneratorService.updateById(id, request);
    });
  }
}
