package com.mahoni.voucherservice.merchant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mahoni.voucherservice.auth.config.JwtTokenUtil;
import com.mahoni.voucherservice.merchant.dto.MerchantRequest;
import com.mahoni.voucherservice.merchant.dto.MerchantResponse;
import com.mahoni.voucherservice.merchant.exception.MerchantNotFoundException;
import com.mahoni.voucherservice.merchant.model.Merchant;
import com.mahoni.voucherservice.merchant.model.MerchantRole;
import com.mahoni.voucherservice.merchant.service.MerchantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
@Import(JwtTokenUtil.class)
@WebMvcTest(MerchantController.class)
public class MerchantControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private WebApplicationContext context;

  @MockBean
  private MerchantService merchantService;

  private Merchant merchant;

  private MerchantRequest request;

  private MerchantResponse response;

  @BeforeEach
  void init() {
    UUID id = UUID.randomUUID();
    merchant = new Merchant();
    merchant.setId(id);
    merchant.setUsername("Test");
    merchant.setName("Test");
    merchant.setEmail("Test@mail.com");
    merchant.setPassword("Test");
    merchant.setRole(MerchantRole.MERCHANT);
    request = new MerchantRequest();
    request.setUsername("Test");
    request.setName("Test");
    request.setEmail("Test@mail.com");
    request.setPassword("Test");
    response = new MerchantResponse();
    response.setId(id);
    response.setUsername("Test");
    response.setName("Test");
    response.setEmail("Test@mail.com");
    mockMvc = MockMvcBuilders
      .webAppContextSetup(context)
      .build();
  }

  @Test
  public void testPost_thenReturnMerchantResponse() throws Exception {
    when(merchantService.create(any())).thenReturn(merchant);

    MvcResult result = this.mockMvc.perform(post("/api/v1/merchants")
        .with(user("Test").authorities(merchant.getAuthorities()))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(response));
    verify(merchantService).create(any());
  }

  @Test
  public void testPost_thenThrowRuntimeException() throws Exception {
    when(merchantService.create(any())).thenThrow(RuntimeException.class);

    this.mockMvc.perform(post("/api/v1/merchants")
        .with(user("Test").authorities(merchant.getAuthorities()))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isBadRequest())
      .andReturn();
  }

  @Test
  public void testGetAll_thenReturnMerchantResponses() throws Exception {
    List<Merchant> merchants = new ArrayList<>();
    List<MerchantResponse> responses = new ArrayList<>();

    when(merchantService.getAll()).thenReturn(merchants);

    MvcResult result = this.mockMvc.perform(get("/api/v1/merchants")
        .with(user("Test").authorities(merchant.getAuthorities())))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(responses));
    verify(merchantService).getAll();
  }

  @Test
  public void testGetById_thenReturnMerchantResponse() throws Exception {
    when(merchantService.getById(any())).thenReturn(merchant);

    MvcResult result = this.mockMvc.perform(get("/api/v1/merchants/{id}", UUID.randomUUID())
        .with(user("Test").authorities(merchant.getAuthorities())))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(response));
    verify(merchantService).getById(any());
  }

  @Test
  public void testGetById_thenThrowMerchantNotFound() throws Exception {
    when(merchantService.getById(any())).thenThrow(MerchantNotFoundException.class);

    this.mockMvc.perform(get("/api/v1/merchants/{id}", UUID.randomUUID())
        .with(user("Test").authorities(merchant.getAuthorities())))
      .andExpect(status().isNotFound())
      .andReturn();
  }

  @Test
  public void testDelete_thenReturnMerchantResponse() throws Exception {
    when(merchantService.deleteById(any())).thenReturn(merchant);

    MvcResult result = this.mockMvc.perform(delete("/api/v1/merchants/{id}", UUID.randomUUID())
        .with(user("Test").authorities(merchant.getAuthorities())))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(response));
    verify(merchantService).deleteById(any());
  }

  @Test
  public void testDelete_thenThrowMerchantNotFound() throws Exception {
    when(merchantService.deleteById(any())).thenThrow(MerchantNotFoundException.class);

    this.mockMvc.perform(delete("/api/v1/merchants/{id}", UUID.randomUUID())
        .with(user("Test").authorities(merchant.getAuthorities())))
      .andExpect(status().isNotFound())
      .andReturn();
  }

  @Test
  public void testDelete_thenThrowAccessDenied() throws Exception {
    when(merchantService.deleteById(any())).thenThrow(AccessDeniedException.class);

    this.mockMvc.perform(delete("/api/v1/merchants/{id}", UUID.randomUUID())
        .with(user("Test").authorities(merchant.getAuthorities())))
      .andExpect(status().isForbidden())
      .andReturn();
  }

  @Test
  public void testUpdate_thenReturnMerchantResponse() throws Exception {
    when(merchantService.update(any(), any())).thenReturn(merchant);

    MvcResult result = this.mockMvc.perform(put("/api/v1/merchants/{id}", UUID.randomUUID())
        .with(user("Test").authorities(merchant.getAuthorities()))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(response));
    verify(merchantService).update(any(), any());
  }

  @Test
  public void testUpdate_thenThrowMerchantNotFound() throws Exception {
    when(merchantService.update(any(), any())).thenThrow(MerchantNotFoundException.class);

    this.mockMvc.perform(put("/api/v1/merchants/{id}", UUID.randomUUID())
        .with(user("Test").authorities(merchant.getAuthorities()))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isNotFound())
      .andReturn();
  }

  @Test
  public void testUpdate_thenThrowAccessDenied() throws Exception {
    when(merchantService.update(any(), any())).thenThrow(AccessDeniedException.class);

    this.mockMvc.perform(put("/api/v1/merchants/{id}", UUID.randomUUID())
        .with(user("Test").authorities(merchant.getAuthorities()))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isForbidden())
      .andReturn();
  }
}
