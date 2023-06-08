package com.mahoni.airqualityservice.repository;

import com.mahoni.airqualityservice.model.AirSensor;
import com.mahoni.airqualityservice.model.Location;
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
    testEntityManager.persist(new AirSensor(4L, "Ohio", location4));

    List<AirSensor> airSensors = airSensorRepository.findAirSensorsByLocation("ohio");

    assertEquals(4, airSensors.size());
    assertEquals(location1, airSensors.get(0).getLocation());
    assertEquals(location2, airSensors.get(1).getLocation());
    assertEquals(location3, airSensors.get(2).getLocation());
    assertEquals("Ohio", airSensors.get(3).getLocationName());
  }
}
