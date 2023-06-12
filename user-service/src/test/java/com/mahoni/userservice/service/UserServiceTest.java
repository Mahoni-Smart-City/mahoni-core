package com.mahoni.userservice.service;

import com.mahoni.userservice.dto.UserRequest;
import com.mahoni.userservice.exception.ResourceAlreadyExistException;
import com.mahoni.userservice.exception.ResourceNotFoundException;
import com.mahoni.userservice.kafka.UserEventProducer;
import com.mahoni.userservice.model.Sex;
import com.mahoni.userservice.model.User;
import com.mahoni.userservice.repository.UserRepository;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  UserRepository userRepository;

  @Mock
  UserEventProducer userEventProducer;

  @InjectMocks
  UserService userService;

  @Captor
  ArgumentCaptor<User> userArgumentCaptor;

  @Test
  public void testGivenUserRequest_thenSaveUser() {
    UserRequest request = new UserRequest("Test", "Test", "Test@mail.com", Sex.NOT_KNOWN, 2000);
    User user = new User(UUID.randomUUID(), "Test", "Test", "Test@mail.com", Sex.NOT_KNOWN, 2000, userService.DEFAULT_USER_POINT);

    when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
    when(userRepository.save(any())).thenReturn(user);
    User savedUser = userService.create(request);

    assertEquals(savedUser, user);
    verify(userRepository).save(any());
  }

  @Test
  public void testGivenUserRequest_thenThrowResourceAlreadyExist() {
    UserRequest request = new UserRequest("Test", "Test", "Test@mail.com", Sex.NOT_KNOWN, 2000);
    User user = new User("Test", "Test", "Test@mail.com", Sex.NOT_KNOWN, 2000, userService.DEFAULT_USER_POINT);

    when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));

    assertThrows(ResourceAlreadyExistException.class, () -> userService.create(request));
  }

  @Test
  public void testGivenId_thenReturnUser() {
    UUID id = UUID.randomUUID();
    User user = new User("Test", "Test", "Test@mail.com", Sex.NOT_KNOWN, 2000, userService.DEFAULT_USER_POINT);

    when(userRepository.findById(any())).thenReturn(Optional.of(user));
    User savedUser = userService.getById(id);

    assertEquals(savedUser, user);
    verify(userRepository).findById(any());
  }

  @Test
  public void testGivenId_thenThrowResourceNotFound() {
    UUID id = UUID.randomUUID();

    when(userRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> userService.getById(id));
  }

  @Test
  public void testGetAll_thenReturnUsers() {
    User user = new User("Test", "Test", "Test@mail.com", Sex.NOT_KNOWN, 2000, userService.DEFAULT_USER_POINT);
    List<User> users = new ArrayList<>();
    users.add(user);

    when(userRepository.findAll()).thenReturn(users);
    List<User> savedUsers = userService.getAll();

    assertEquals(savedUsers, users);
    verify(userRepository).findAll();
  }

  @Test
  public void testGivenIdToBeDeleted_thenDeleteAndReturnDeletedUser() {
    UUID id = UUID.randomUUID();
    User user = new User("Test", "Test", "Test@mail.com", Sex.NOT_KNOWN, 2000, userService.DEFAULT_USER_POINT);

    when(userRepository.findById(any())).thenReturn(Optional.of(user));
    User deletedUser = userService.deleteById(id);

    assertEquals(deletedUser, user);
    verify(userRepository).deleteById(any());
  }

  @Test
  public void testGivenIdToBeDeleted_thenThrowResourceNotFound() {
    UUID id = UUID.randomUUID();

    when(userRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> userService.deleteById(id));
  }

  @Test
  public void testGivenIdAndUserRequest_thenUpdateAndReturnUpdatedUser() {
    UUID id = UUID.randomUUID();
    UserRequest request = new UserRequest("Test2", "Test", "Test@mail.com", Sex.NOT_KNOWN, 2000);
    User user = new User("Test", "Test", "Test@mail.com", Sex.NOT_KNOWN, 2000, userService.DEFAULT_USER_POINT);
    User expectedUser = new User("Test2", "Test", "Test@mail.com", Sex.NOT_KNOWN, 2000, userService.DEFAULT_USER_POINT);

    when(userRepository.findById(any())).thenReturn(Optional.of(user));
    when(userRepository.save(any())).thenReturn(expectedUser);
    User updatedUser = userService.update(id, request);

    assertEquals(updatedUser, expectedUser);
    verify(userRepository).save(userArgumentCaptor.capture());
    assertEquals(userArgumentCaptor.getValue().getUsername(), expectedUser.getUsername());
  }

  @Test
  public void testGivenIdAndUserRequest_thenThrowResourceNotFound() {
    UUID id = UUID.randomUUID();
    UserRequest request = new UserRequest("Test2", "Test", "Test@mail.com", Sex.NOT_KNOWN, 2000);

    when(userRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> userService.update(id, request));
  }
}
