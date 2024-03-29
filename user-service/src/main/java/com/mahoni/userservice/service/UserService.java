package com.mahoni.userservice.service;

import com.mahoni.schema.AirQualityRawSchema;
import com.mahoni.userservice.dto.UserRequest;
import com.mahoni.userservice.exception.ResourceAlreadyExistException;
import com.mahoni.userservice.exception.ResourceNotFoundException;
import com.mahoni.userservice.kafka.UserEventProducer;
import com.mahoni.userservice.model.User;
import com.mahoni.userservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UserService {

  @Autowired
  UserRepository userRepository;

  @Autowired
  UserEventProducer userEventProducer;

  final Integer DEFAULT_USER_POINT = 0;

  public User create(UserRequest user) {
    if (userRepository.findByUsername(user.getUsername()).isPresent()) {
      throw  new ResourceAlreadyExistException(user.getUsername());
    }
    User savedUser = userRepository.save(new User(user.getUsername(), user.getName(), user.getEmail(), user.getSex(), user.getYearOfBirth(), DEFAULT_USER_POINT));
    userEventProducer.send(savedUser, DEFAULT_USER_POINT, savedUser.getId().toString());
    return savedUser;
  }

  public User getById(UUID id) {
    Optional<User> user = userRepository.findById(id);
    if (user.isEmpty()) {
      throw new ResourceNotFoundException(id);
    }
    return user.get();
  }

  public List<User> getAll() {
    return userRepository.findAll();
  }

  public User deleteById(UUID id) {
    Optional<User> user = userRepository.findById(id);
    if (user.isEmpty()) {
      throw new ResourceNotFoundException(id);
    }
    userRepository.deleteById(id);
    return user.get();
  }

  public User update(UUID id, UserRequest newUser) {
    Optional<User> user = userRepository.findById(id);
    if (user.isEmpty()) {
      throw new ResourceNotFoundException(id);
    }
    User updatedUser = user.get();
    updatedUser.setUsername(newUser.getUsername());
    updatedUser.setEmail(newUser.getEmail());
    updatedUser.setName(newUser.getName());
    updatedUser.setSex(newUser.getSex());
    updatedUser.setYearOfBirth(newUser.getYearOfBirth());
    return userRepository.save(updatedUser);
  }

  @KafkaListener(topics = "air-quality-raw-topic")
  public void test(AirQualityRawSchema airQualityRawSchema){
    System.out.println(airQualityRawSchema.getAqi());
  }
}
