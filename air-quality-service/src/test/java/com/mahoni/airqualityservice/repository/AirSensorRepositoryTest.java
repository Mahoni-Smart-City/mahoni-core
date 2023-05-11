package com.mahoni.airqualityservice.repository;

import com.mahoni.airqualityservice.model.AirSensor;
import com.mahoni.airqualityservice.model.Location;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

  @Test
  public void testFindAirSensorsByLocation() {
    Location location1 = new Location(1L, "Ohio A", "Test", "Test", 12.34, 12.34);
    Location location2 = new Location(2L, "Test", "B Ohio", "Test", 12.34, 12.34);
    Location location3 = new Location(3L, "Test", "Test", "Ohio", 12.34, 12.34);
    Location location4 = new Location(4L, "Test", "Test", "Test", 12.34, 12.34);

    testEntityManager.persist(location1);
    testEntityManager.persist(location2);
    testEntityManager.persist(location3);
    testEntityManager.persist(location4);
    testEntityManager.persist(new AirSensor(1L, "Test", location1));
    testEntityManager.persist(new AirSensor(2L, "Test", location2));
    testEntityManager.persist(new AirSensor(3L, "Test", location3));
    testEntityManager.persist(new AirSensor(4L, "Test", location4));

    Optional<List<AirSensor>> airSensors = airSensorRepository.findAirSensorsByLocation("Ohio");

    assertTrue(airSensors.isPresent());
    assertEquals(3, airSensors.get().size());
    assertEquals(location1, airSensors.get().get(0).getLocation());
    assertEquals(location2, airSensors.get().get(1).getLocation());
    assertEquals(location3, airSensors.get().get(2).getLocation());
  }
}
