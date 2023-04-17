package com.mahoni.airqualityservice.service;

import com.mahoni.airqualityservice.dto.AirSensorRequest;
import com.mahoni.airqualityservice.exception.AirSensorAlreadyExistException;
import com.mahoni.airqualityservice.exception.AirSensorNotFoundException;
import com.mahoni.airqualityservice.exception.LocationNotFoundException;
import com.mahoni.airqualityservice.model.AirSensor;
import com.mahoni.airqualityservice.model.Location;
import com.mahoni.airqualityservice.repository.AirSensorRepository;
import com.mahoni.airqualityservice.repository.LocationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AirSensorServiceTest {

  @Mock
  AirSensorRepository airSensorRepository;

  @Mock
  LocationRepository locationRepository;

  @InjectMocks
  AirSensorService airSensorService;

  @Captor
  ArgumentCaptor<AirSensor> airSensorArgumentCaptor;

  @Test
  public void testGivenAirSensorRequest_thenSaveAirSensor() {
    Long id = 1L;
    AirSensorRequest request = new AirSensorRequest(id, "Test", id);
    Location location = new Location(id, "Test", "Test", "Test", "Test", "Test");
    AirSensor airSensor = new AirSensor(id, "Test", location);

    when(locationRepository.findById(any())).thenReturn(Optional.of(location));
    when(airSensorRepository.save(any())).thenReturn(airSensor);
    AirSensor savedAirSensor = airSensorService.create(request);

    assertEquals(savedAirSensor, airSensor);
    verify(airSensorRepository).save(any());
  }

  @Test
  public void testGivenAirSensorRequest_thenThrowAirSensorAlreadyExist() {
    Long id = 1L;
    AirSensorRequest request = new AirSensorRequest(id, "Test", id);
    Location location = new Location(id, "Test", "Test", "Test", "Test", "Test");
    AirSensor airSensor = new AirSensor(id, "Test", location);

    when(airSensorRepository.findById(any())).thenReturn(Optional.of(airSensor));

    assertThrows(AirSensorAlreadyExistException.class, () -> airSensorService.create(request));
  }

  @Test
  public void testGivenAirSensorRequest_thenThrowLocationNotFound() {
    Long id = 1L;
    AirSensorRequest request = new AirSensorRequest(id, "Test", id);

    when(locationRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(LocationNotFoundException.class, () -> airSensorService.create(request));
  }

  @Test
  public void testGivenId_thenReturnAirSensor() {
    Long id = 1L;
    Location location = new Location(id, "Test", "Test", "Test", "Test", "Test");
    AirSensor airSensor = new AirSensor(id, "Test", location);

    when(airSensorRepository.findById(any())).thenReturn(Optional.of(airSensor));
    AirSensor savedAirSensor = airSensorService.getById(id);

    assertEquals(savedAirSensor, airSensor);
    verify(airSensorRepository).findById(any());
  }

  @Test
  public void testGivenId_thenReturnAirSensorNotFound() {
    Long id = 1L;

    when(airSensorRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(AirSensorNotFoundException.class, () -> airSensorService.getById(id));
  }

  @Test
  public void testGetAll_thenReturnAirSensors() {
    Long id = 1L;
    Location location = new Location(id, "Test", "Test", "Test", "Test", "Test");
    AirSensor airSensor = new AirSensor(id, "Test", location);
    List<AirSensor> airSensors = new ArrayList<>();
    airSensors.add(airSensor);

    when(airSensorRepository.findAll()).thenReturn(airSensors);
    List<AirSensor> savedAirSensors = airSensorService.getAll();

    assertEquals(savedAirSensors, airSensors);
    verify(airSensorRepository).findAll();
  }

  @Test
  public void testGivenIdToBeDeleted_thenDeleteAndReturnDeletedAirSensor() {
    Long id = 1L;
    Location location = new Location(id, "Test", "Test", "Test", "Test", "Test");
    AirSensor airSensor = new AirSensor(id, "Test", location);

    when(airSensorRepository.findById(any())).thenReturn(Optional.of(airSensor));
    AirSensor deletedAirSensor = airSensorService.deleteById(id);

    assertEquals(deletedAirSensor, airSensor);
    verify(airSensorRepository).deleteById(any());
  }

  @Test
  public void testGivenIdToBeDeleted_thenThrowAirSensorNotFound() {
    Long id = 1L;

    when(airSensorRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(AirSensorNotFoundException.class, () -> airSensorService.deleteById(id));
  }

  @Test
  public void testGivenIdAndAirSensorRequest_thenUpdateAndReturnUpdatedAirSensor() {
    Long id = 1L;
    AirSensorRequest request = new AirSensorRequest(id, "Test2", id);
    Location location = new Location(id, "Test", "Test", "Test", "Test", "Test");
    AirSensor airSensor = new AirSensor(id, "Test", location);
    AirSensor expectedAirSensor = new AirSensor(id, "Test2", location);

    when(airSensorRepository.findById(any())).thenReturn(Optional.of(airSensor));
    when(locationRepository.findById(any())).thenReturn(Optional.of(location));
    when(airSensorRepository.save(any())).thenReturn(expectedAirSensor);
    AirSensor updatedAirSensor = airSensorService.update(id, request);

    assertEquals(updatedAirSensor, expectedAirSensor);
    verify(airSensorRepository).save(airSensorArgumentCaptor.capture());
    assertEquals(airSensorArgumentCaptor.getValue().getNameLocation(), expectedAirSensor.getNameLocation());
  }

  @Test
  public void testGivenIdAndAirSensorRequest_thenThrowAirSensorNotFound() {
    Long id = 1L;
    AirSensorRequest request = new AirSensorRequest(id, "Test", id);

    when(airSensorRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(AirSensorNotFoundException.class, () -> airSensorService.update(id, request));
  }

  @Test
  public void testGivenIdAndAirSensorRequest_thenThrowLocationNotFound() {
    Long id = 1L;
    AirSensorRequest request = new AirSensorRequest(id, "Test", id);
    Location location = new Location(id, "Test", "Test", "Test", "Test", "Test");
    AirSensor airSensor = new AirSensor(id, "Test", location);

    when(airSensorRepository.findById(any())).thenReturn(Optional.of(airSensor));
    when(locationRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(LocationNotFoundException.class, () -> airSensorService.update(id, request));
  }
}
