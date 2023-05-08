package com.mahoni.voucherservice.voucher.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mahoni.voucherservice.auth.config.JwtTokenUtil;
import com.mahoni.voucherservice.merchant.model.Merchant;
import com.mahoni.voucherservice.merchant.model.MerchantRole;
import com.mahoni.voucherservice.voucher.dto.VoucherRequest;
import com.mahoni.voucherservice.voucher.dto.VoucherResponse;
import com.mahoni.voucherservice.voucher.exception.VoucherNotFoundException;
import com.mahoni.voucherservice.voucher.model.Voucher;
import com.mahoni.voucherservice.voucher.model.VoucherType;
import com.mahoni.voucherservice.voucher.service.VoucherService;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({MockitoExtension.class})
@Import(JwtTokenUtil.class)
@WebMvcTest(VoucherController.class)
public class VoucherControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private WebApplicationContext context;

  @MockBean
  private VoucherService voucherService;

  private Merchant merchant;

  private Voucher voucher;

  private VoucherRequest request;

  private VoucherResponse response;

  @BeforeEach
  void init() {
    UUID id = UUID.randomUUID();
    LocalDateTime time = LocalDateTime.now();
    merchant = new Merchant(id, "Test", "Test", "Test@mail.com", "Test", MerchantRole.MERCHANT);
    request = new VoucherRequest("Test", "Test", VoucherType.FNB, 1, time, time);
    voucher = new Voucher(id, "Test", "Test", VoucherType.FNB, 1, time, time, merchant, 0);
    response = new VoucherResponse();
    response.setId(id);
    response.setName("Test");
    response.setDescription("Test");
    response.setType(VoucherType.FNB);
    response.setPoint(1);
    response.setStartAt(time);
    response.setExpiredAt(time);
    response.setMerchantId(id);
    response.setQuantity(0);
    mockMvc = MockMvcBuilders
      .webAppContextSetup(context)
      .build();
  }

  @Test
  public void testPost_thenReturnVoucherResponse() throws Exception {
    when(voucherService.create(any())).thenReturn(voucher);

    MvcResult result = this.mockMvc.perform(post("/api/v1/vouchers")
        .with(user("Test").authorities(merchant.getAuthorities()))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(response));
    verify(voucherService).create(any());
  }

  @Test
  public void testPost_thenThrowRuntimeException() throws Exception {
    when(voucherService.create(any())).thenThrow(RuntimeException.class);

    this.mockMvc.perform(post("/api/v1/vouchers")
        .with(user("Test").authorities(merchant.getAuthorities()))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isBadRequest())
      .andReturn();
  }

  @Test
  public void testGetAll_thenReturnVoucherResponses() throws Exception {
    List<Voucher> vouchers = new ArrayList<>();
    List<VoucherResponse> responses = new ArrayList<>();

    when(voucherService.getAll()).thenReturn(vouchers);

    MvcResult result = this.mockMvc.perform(get("/api/v1/vouchers")
        .with(user("Test").authorities(merchant.getAuthorities())))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(responses));
    verify(voucherService).getAll();
  }

  @Test
  public void testGetById_thenReturnVoucherResponse() throws Exception {
    when(voucherService.getById(any())).thenReturn(voucher);

    MvcResult result = this.mockMvc.perform(get("/api/v1/vouchers/{id}", UUID.randomUUID())
        .with(user("Test").authorities(merchant.getAuthorities())))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(response));
    verify(voucherService).getById(any());
  }

  @Test
  public void testGetById_thenThrowVoucherNotFound() throws Exception {
    when(voucherService.getById(any())).thenThrow(VoucherNotFoundException.class);

    this.mockMvc.perform(get("/api/v1/vouchers/{id}", UUID.randomUUID())
        .with(user("Test").authorities(merchant.getAuthorities())))
      .andExpect(status().isNotFound())
      .andReturn();
  }

  @Test
  public void testDelete_thenReturnVoucherResponse() throws Exception {
    when(voucherService.deleteById(any())).thenReturn(voucher);

    MvcResult result = this.mockMvc.perform(delete("/api/v1/vouchers/{id}", UUID.randomUUID())
        .with(user("Test").authorities(merchant.getAuthorities())))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(response));
    verify(voucherService).deleteById(any());
  }

  @Test
  public void testDelete_thenThrowVoucherNotFound() throws Exception {
    when(voucherService.deleteById(any())).thenThrow(VoucherNotFoundException.class);

    this.mockMvc.perform(delete("/api/v1/vouchers/{id}", UUID.randomUUID())
        .with(user("Test").authorities(merchant.getAuthorities())))
      .andExpect(status().isNotFound())
      .andReturn();
  }

  @Test
  public void testDelete_thenThrowAccessDenied() throws Exception {
    when(voucherService.deleteById(any())).thenThrow(AccessDeniedException.class);

    this.mockMvc.perform(delete("/api/v1/vouchers/{id}", UUID.randomUUID())
        .with(user("Test").authorities(merchant.getAuthorities())))
      .andExpect(status().isForbidden())
      .andReturn();
  }

  @Test
  public void testUpdate_thenReturnVoucherResponse() throws Exception {
    when(voucherService.update(any(), any())).thenReturn(voucher);

    MvcResult result = this.mockMvc.perform(put("/api/v1/vouchers/{id}", UUID.randomUUID())
        .with(user("Test").authorities(merchant.getAuthorities()))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(response));
    verify(voucherService).update(any(), any());
  }

  @Test
  public void testUpdate_thenThrowVoucherNotFound() throws Exception {
    when(voucherService.update(any(), any())).thenThrow(VoucherNotFoundException.class);

    this.mockMvc.perform(put("/api/v1/vouchers/{id}", UUID.randomUUID())
        .with(user("Test").authorities(merchant.getAuthorities()))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isNotFound())
      .andReturn();
  }

  @Test
  public void testUpdate_thenThrowAccessDenied() throws Exception {
    when(voucherService.update(any(), any())).thenThrow(AccessDeniedException.class);

    this.mockMvc.perform(put("/api/v1/vouchers/{id}", UUID.randomUUID())
        .with(user("Test").authorities(merchant.getAuthorities()))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isForbidden())
      .andReturn();
  }
}
