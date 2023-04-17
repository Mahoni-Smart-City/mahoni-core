package com.mahoni.userservice.controller;

import com.mahoni.userservice.dto.UserRequest;
import com.mahoni.userservice.exception.ResourceNotFoundException;
import com.mahoni.userservice.model.User;
import com.mahoni.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@Slf4j
public class UserController {

  @Autowired
    UserService userService;

    @PostMapping
    public ResponseEntity<User> post(@Valid @RequestBody UserRequest request) {
      try {
        User newUser = userService.create(request);
        return ResponseEntity.ok(newUser);
      } catch (RuntimeException e) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
      }
    }

    @GetMapping
    private ResponseEntity<List<User>> getAll() {
      List<User> allUsers = userService.getAll();
      return ResponseEntity.ok(allUsers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> get(@PathVariable("id") UUID id ) {
      try {
        User user = userService.getById(id);
        return ResponseEntity.ok(user);
      } catch (ResourceNotFoundException e) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
      }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<User> delete(@PathVariable("id") UUID id) {
      try {
        User deletedUser = userService.deleteById(id);
        return ResponseEntity.ok(deletedUser);
      } catch (ResourceNotFoundException e) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
      }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable("id") UUID id, @Valid @RequestBody UserRequest request) {
      try {
        User updatedUser = userService.update(id, request);
        return ResponseEntity.ok(updatedUser);
      } catch (ResourceNotFoundException e) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
      }
    }
}
