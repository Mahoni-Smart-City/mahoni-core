package com.mahoni.airqualityservice.repository;

import com.mahoni.airqualityservice.model.AirSensor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
public class AirSensorRepositoryTest {

  @Autowired
  private AirSensorRepository airSensorRepository;

  @Autowired
  private TestEntityManager testEntityManager;

  @Test
  public void testFindAll() {
    AirSensor airSensor1 = new AirSensor();
    AirSensor airSensor2 = new AirSensor();
    airSensor1.setId(1L);
    airSensor2.setId(2L);
    testEntityManager.persist(airSensor1);
    testEntityManager.persist(airSensor2);

    List<AirSensor> airSensors = airSensorRepository.findAll();

    assertFalse(airSensors.isEmpty());
    assertEquals(2, airSensors.size());
  }
}
