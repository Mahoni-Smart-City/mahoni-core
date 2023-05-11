package com.mahoni.voucherservice.voucher.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mahoni.voucherservice.auth.config.JwtTokenUtil;
import com.mahoni.voucherservice.merchant.model.Merchant;
import com.mahoni.voucherservice.merchant.model.MerchantRole;
import com.mahoni.voucherservice.voucher.dto.RedeemVoucherRequestCRUD;
import com.mahoni.voucherservice.voucher.exception.RedeemVoucherNotFoundException;
import com.mahoni.voucherservice.voucher.model.RedeemVoucher;
import com.mahoni.voucherservice.voucher.model.Voucher;
import com.mahoni.voucherservice.voucher.model.VoucherType;
import com.mahoni.voucherservice.voucher.service.RedeemVoucherService;
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


@ExtendWith(MockitoExtension.class)
@Import(JwtTokenUtil.class)
@WebMvcTest(DetailVoucherController.class)
public class DetailVoucherControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private WebApplicationContext context;

  @MockBean
  private RedeemVoucherService redeemVoucherService;

  private Merchant merchant;

  private RedeemVoucher redeemVoucher;

  private RedeemVoucherRequestCRUD request;

  @BeforeEach
  void init() {
    UUID id = UUID.randomUUID();
    merchant = new Merchant(id, "Test", "Test", "Test@mail.com", "Test", MerchantRole.MERCHANT);
    Voucher voucher = new Voucher(id, "Test", "Test", VoucherType.FOOD_AND_BEVERAGES, 1, LocalDateTime.now(), LocalDateTime.now(), merchant, 0);
    redeemVoucher = new RedeemVoucher(voucher, "Test", LocalDateTime.now());
    request = new RedeemVoucherRequestCRUD(id,"Test", LocalDateTime.now());
    mockMvc = MockMvcBuilders
      .webAppContextSetup(context)
      .build();
  }

  @Test
  public void testPost_thenReturnRedeemVoucher() throws Exception {
    when(redeemVoucherService.create(any())).thenReturn(redeemVoucher);

    MvcResult result = this.mockMvc.perform(post("/api/v1/vouchers/detail")
        .with(user("Test").authorities(merchant.getAuthorities()))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(redeemVoucher));
    verify(redeemVoucherService).create(any());
  }

  @Test
  public void testPost_thenThrowRuntimeException() throws Exception {
    when(redeemVoucherService.create(any())).thenThrow(RuntimeException.class);

    this.mockMvc.perform(post("/api/v1/vouchers/detail")
        .with(user("Test").authorities(merchant.getAuthorities()))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isBadRequest())
      .andReturn();
  }

  @Test
  public void testGetAll_thenReturnRedeemVouchers() throws Exception {
    List<RedeemVoucher> redeemVouchers = new ArrayList<>();

    when(redeemVoucherService.getAll()).thenReturn(redeemVouchers);

    MvcResult result = this.mockMvc.perform(get("/api/v1/vouchers/detail")
        .with(user("Test").authorities(merchant.getAuthorities())))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(redeemVouchers));
    verify(redeemVoucherService).getAll();
  }

  @Test
  public void testGetById_thenReturnRedeemVouchers() throws Exception {
    when(redeemVoucherService.getById(any())).thenReturn(redeemVoucher);

    MvcResult result = this.mockMvc.perform(get("/api/v1/vouchers/detail/{id}", UUID.randomUUID())
        .with(user("Test").authorities(merchant.getAuthorities())))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(redeemVoucher));
    verify(redeemVoucherService).getById(any());
  }

  @Test
  public void testGetById_thenThrowRedeemVoucherNotFound() throws Exception {
    when(redeemVoucherService.getById(any())).thenThrow(RedeemVoucherNotFoundException.class);

    this.mockMvc.perform(get("/api/v1/vouchers/detail/{id}", UUID.randomUUID())
        .with(user("Test").authorities(merchant.getAuthorities())))
      .andExpect(status().isNotFound())
      .andReturn();
  }

  @Test
  public void testGetById_thenThrowAccessDenied() throws Exception {
    when(redeemVoucherService.getById(any())).thenThrow(AccessDeniedException.class);

    this.mockMvc.perform(get("/api/v1/vouchers/detail/{id}", UUID.randomUUID())
        .with(user("Test").authorities(merchant.getAuthorities())))
      .andExpect(status().isForbidden())
      .andReturn();
  }
  @Test
  public void testDelete_thenReturnDeletedRedeemVoucher() throws Exception {
    when(redeemVoucherService.deleteById(any())).thenReturn(redeemVoucher);

    MvcResult result = this.mockMvc.perform(delete("/api/v1/vouchers/detail/{id}", UUID.randomUUID())
        .with(user("Test").authorities(merchant.getAuthorities())))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(redeemVoucher));
    verify(redeemVoucherService).deleteById(any());
  }

  @Test
  public void testDelete_thenThrowRedeemVoucherNotFound() throws Exception {
    when(redeemVoucherService.deleteById(any())).thenThrow(RedeemVoucherNotFoundException.class);

    this.mockMvc.perform(delete("/api/v1/vouchers/detail/{id}", UUID.randomUUID())
        .with(user("Test").authorities(merchant.getAuthorities())))
      .andExpect(status().isNotFound())
      .andReturn();
  }

  @Test
  public void testDelete_thenThrowAccessDenied() throws Exception {
    when(redeemVoucherService.deleteById(any())).thenThrow(AccessDeniedException.class);

    this.mockMvc.perform(delete("/api/v1/vouchers/detail/{id}", UUID.randomUUID())
        .with(user("Test").authorities(merchant.getAuthorities())))
      .andExpect(status().isForbidden())
      .andReturn();
  }

  @Test
  public void testUpdate_thenReturnRedeemVoucherResponse() throws Exception {
    when(redeemVoucherService.update(any(), any())).thenReturn(redeemVoucher);

    MvcResult result = this.mockMvc.perform(put("/api/v1/vouchers/detail/{id}", UUID.randomUUID())
        .with(user("Test").authorities(merchant.getAuthorities()))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(redeemVoucher));
    verify(redeemVoucherService).update(any(), any());
  }

  @Test
  public void testUpdate_thenThrowRedeemVoucherNotFound() throws Exception {
    when(redeemVoucherService.update(any(), any())).thenThrow(RedeemVoucherNotFoundException.class);

    this.mockMvc.perform(put("/api/v1/vouchers/detail/{id}", UUID.randomUUID())
        .with(user("Test").authorities(merchant.getAuthorities()))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isNotFound())
      .andReturn();
  }

  @Test
  public void testUpdate_thenThrowAccessDenied() throws Exception {
    when(redeemVoucherService.update(any(), any())).thenThrow(AccessDeniedException.class);

    this.mockMvc.perform(put("/api/v1/vouchers/detail/{id}", UUID.randomUUID())
        .with(user("Test").authorities(merchant.getAuthorities()))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isForbidden())
      .andReturn();
  }
}
