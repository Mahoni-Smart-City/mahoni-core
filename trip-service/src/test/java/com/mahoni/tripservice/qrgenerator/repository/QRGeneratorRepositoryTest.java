package com.mahoni.tripservice.qrgenerator.repository;

import com.mahoni.tripservice.qrgenerator.dto.QRGeneratorType;
import com.mahoni.tripservice.qrgenerator.model.QRGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
public class QRGeneratorRepositoryTest {

  @Autowired
  private QRGeneratorRepository qrGeneratorRepository;

  @Autowired
  private TestEntityManager testEntityManager;

  @Test
  public void testFindAll() {
    testEntityManager.persist(new QRGenerator("Test", QRGeneratorType.MRT.name(), UUID.randomUUID()));
    testEntityManager.persist(new QRGenerator("Test", QRGeneratorType.JAKLINGKO.name(), UUID.randomUUID()));

    List<QRGenerator> qrGenerators = qrGeneratorRepository.findAll();

    assertFalse(qrGenerators.isEmpty());
    assertEquals(2, qrGenerators.size());
  }
}
