package com.mahoni.airqualityservice.repository;

import com.mahoni.airqualityservice.model.Location;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
public class LocationRepositoryTest {

  @Autowired
  private LocationRepository locationRepository;

  @Autowired
  private TestEntityManager testEntityManager;

  @Test
  public void testFindAll() {
    testEntityManager.persist(new Location(1L, "Test", "Test", "Test", "Test", "Test"));
    testEntityManager.persist(new Location(2L, "Test", "Test", "Test", "Test", "Test"));

    List<Location> locations = locationRepository.findAll();

    assertFalse(locations.isEmpty());
    assertEquals(2, locations.size());
  }
}
