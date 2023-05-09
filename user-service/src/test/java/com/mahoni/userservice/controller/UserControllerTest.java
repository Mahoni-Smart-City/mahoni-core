package com.mahoni.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mahoni.userservice.dto.UserRequest;
import com.mahoni.userservice.exception.ResourceNotFoundException;
import com.mahoni.userservice.model.Sex;
import com.mahoni.userservice.model.User;
import com.mahoni.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private UserService userService;

  @Test
  public void testPost_thenReturnNewUser() throws Exception {
    User user = new User();
    user.setId(UUID.randomUUID());
    user.setUsername("Test");
    user.setName("Test");
    user.setEmail("test@mail.com");
    user.setSex(Sex.NOT_KNOWN);
    user.setYearOfBirth(2000);
    user.setPoint(0);
    UserRequest request = new UserRequest();
    request.setUsername("Test");
    request.setName("Test");
    request.setEmail("test@mail.com");
    request.setSex(Sex.NOT_KNOWN);
    request.setYearOfBirth(2000);

    when(userService.create(any())).thenReturn(user);

    MvcResult result = this.mockMvc.perform(post("/api/v1/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(user));
    verify(userService).create(any());
  }

  @Test
  public void testPost_thenThrowRuntimeException() throws Exception {
    UserRequest request = new UserRequest("Test", "Test", "test@mail.com", Sex.NOT_KNOWN, 2000);

    when(userService.create(any())).thenThrow(RuntimeException.class);

    this.mockMvc.perform(post("/api/v1/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isBadRequest())
      .andReturn();
  }

  @Test
  public void testGetAll_thenReturnUsers() throws Exception {
    List<User> users = new ArrayList<>();

    when(userService.getAll()).thenReturn(users);

    MvcResult result = this.mockMvc.perform(get("/api/v1/users"))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(users));
    verify(userService).getAll();
  }

  @Test
  public void testGetById_thenReturnUser() throws Exception {
    User user = new User(UUID.randomUUID(), "Test", "Test", "Test@mail.com", Sex.NOT_KNOWN, 2000, 0);
    when(userService.getById(any())).thenReturn(user);

    MvcResult result = this.mockMvc.perform(get("/api/v1/users/{id}", UUID.randomUUID()))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(user));
    verify(userService).getById(any());
  }

  @Test
  public void testGetById_thenThrowResourceNotFound() throws Exception {
    when(userService.getById(any())).thenThrow(ResourceNotFoundException.class);

    this.mockMvc.perform(get("/api/v1/users/{id}", UUID.randomUUID()))
      .andExpect(status().isNotFound())
      .andReturn();
  }

  @Test
  public void testDelete_thenReturnUser() throws Exception {
    User user = new User("Test", "Test", "Test@mail.com", Sex.NOT_KNOWN, 2000, 0);

    when(userService.deleteById(any())).thenReturn(user);

    MvcResult result = this.mockMvc.perform(delete("/api/v1/users/{id}", UUID.randomUUID()))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(user));
    verify(userService).deleteById(any());
  }

  @Test
  public void testDelete_thenThrowResourceNotFound() throws Exception {
    when(userService.deleteById(any())).thenThrow(ResourceNotFoundException.class);

    this.mockMvc.perform(delete("/api/v1/users/{id}", UUID.randomUUID()))
      .andExpect(status().isNotFound())
      .andReturn();
  }

  @Test
  public void testUpdate_thenReturnUser() throws Exception {
    User user = new User("Test", "Test", "Test@mail.com", Sex.NOT_KNOWN, 2000, 0);
    UserRequest request = new UserRequest("Test", "Test", "test@mail.com", Sex.NOT_KNOWN, 2000);

    when(userService.update(any(), any())).thenReturn(user);

    MvcResult result = this.mockMvc.perform(put("/api/v1/users/{id}", UUID.randomUUID())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(user));
    verify(userService).update(any(), any());
  }

  @Test
  public void testUpdate_thenThrowUserNotFound() throws Exception {
    UserRequest request = new UserRequest("Test", "Test", "test@mail.com", Sex.NOT_KNOWN, 2000);

    when(userService.update(any(), any())).thenThrow(ResourceNotFoundException.class);

    this.mockMvc.perform(put("/api/v1/users/{id}", UUID.randomUUID())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isNotFound())
      .andReturn();
  }
}
