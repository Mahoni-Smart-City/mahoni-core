package com.mahoni.voucherservice.voucher.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mahoni.schema.UserPointTableSchema;
import com.mahoni.voucherservice.auth.config.JwtTokenUtil;
import com.mahoni.voucherservice.voucher.dto.RedeemVoucherRequest;
import com.mahoni.voucherservice.voucher.dto.RedeemVoucherResponse;
import com.mahoni.voucherservice.voucher.exception.RedeemVoucherNotFoundException;
import com.mahoni.voucherservice.voucher.kafka.VoucherServiceStream;
import com.mahoni.voucherservice.voucher.model.RedeemVoucher;
import com.mahoni.voucherservice.voucher.model.Voucher;
import com.mahoni.voucherservice.voucher.model.VoucherStatus;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@Import(JwtTokenUtil.class)
@WebMvcTest(RedeemVoucherController.class)
public class RedeemVoucherControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private WebApplicationContext context;

  @MockBean
  private RedeemVoucherService redeemVoucherService;

  @MockBean
  private VoucherServiceStream voucherServiceStream;

  private RedeemVoucher redeemVoucher;

  private RedeemVoucherRequest request;

  private RedeemVoucherResponse response;

  @BeforeEach
  void init() {
    UUID id = UUID.randomUUID();
    LocalDateTime time = LocalDateTime.now();
    Voucher voucher = new Voucher();
    voucher.setId(id);
    redeemVoucher = new RedeemVoucher(id, voucher, id, "Test", VoucherStatus.ACTIVE, time, time);
    request = new RedeemVoucherRequest(id, id);
    response = new RedeemVoucherResponse();
    response.setId(id);
    response.setVoucherId(id);
    response.setUserId(id);
    response.setRedeemCode("Test");
    response.setStatus(VoucherStatus.ACTIVE);
    response.setRedeemedAt(time);
    response.setExpiredAt(time);

    mockMvc = MockMvcBuilders
      .webAppContextSetup(context)
      .build();
  }

  @Test
  public void testPostPendingVoucher_thenReturnRedeemVoucherResponseWithHiddenCode() throws Exception {
    redeemVoucher.setStatus(VoucherStatus.PENDING);
    response.setRedeemCode("");
    response.setStatus(VoucherStatus.PENDING);
    when(redeemVoucherService.redeem(any())).thenReturn(redeemVoucher);

    MvcResult result = this.mockMvc.perform(post("/api/v1/redeem")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request))
      )
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(response));
    verify(redeemVoucherService).redeem(any());
  }

  @Test
  public void testPost_thenThrowRuntimeException() throws Exception {
    when(redeemVoucherService.redeem(any())).thenThrow(RuntimeException.class);

    this.mockMvc.perform(post("/api/v1/redeem")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isBadRequest())
      .andReturn();
  }

  @Test
  public void testSetRedeemedPendingVoucher_thenReturnRedeemVoucherResponseWithHiddenCode() throws Exception {
    redeemVoucher.setStatus(VoucherStatus.PENDING);
    response.setRedeemCode("");
    response.setStatus(VoucherStatus.PENDING);
    when(redeemVoucherService.setRedeemed(any())).thenReturn(redeemVoucher);

    MvcResult result = this.mockMvc.perform(post("/api/v1/redeem/{id}", UUID.randomUUID()))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(response));
    verify(redeemVoucherService).setRedeemed(any());
  }

  @Test
  public void testSetRedeemed_thenThrowRedeemVoucherNotFoundException() throws Exception {
    when(redeemVoucherService.setRedeemed(any())).thenThrow(RedeemVoucherNotFoundException.class);

    this.mockMvc.perform(post("/api/v1/redeem/{id}", UUID.randomUUID()))
      .andExpect(status().isNotFound())
      .andReturn();
  }

  @Test
  public void testGetAllByUserId_thenReturnRedeemVoucherResponsesWithHiddenCode() throws Exception {
    List<RedeemVoucher> redeemVouchers = new ArrayList<>();
    List<RedeemVoucherResponse> responses = new ArrayList<>();

    redeemVouchers.add(redeemVoucher);
    responses.add(mapper(redeemVoucher));

    when(redeemVoucherService.getAllByUserId(any())).thenReturn(redeemVouchers);

    MvcResult result = this.mockMvc.perform(get("/api/v1/redeem/user/{id}", UUID.randomUUID()))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(responses));
    verify(redeemVoucherService).getAllByUserId(any());
  }

  @Test
  public void testGetAllByUserId_thenReturnRedeemVoucherResponses() throws Exception {
    List<RedeemVoucher> redeemVouchers = new ArrayList<>();
    List<RedeemVoucherResponse> responses = new ArrayList<>();

    redeemVoucher.setStatus(VoucherStatus.PENDING);
    redeemVouchers.add(redeemVoucher);
    responses.add(mapper(redeemVoucher));

    when(redeemVoucherService.getAllByUserId(any())).thenReturn(redeemVouchers);

    MvcResult result = this.mockMvc.perform(get("/api/v1/redeem/user/{id}", UUID.randomUUID()))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(responses));
    verify(redeemVoucherService).getAllByUserId(any());
  }

  private RedeemVoucherResponse mapper(RedeemVoucher redeemVoucher) {
    String code = redeemVoucher.getStatus() == VoucherStatus.PENDING ? "" : redeemVoucher.getRedeemCode();
    return new RedeemVoucherResponse(
      redeemVoucher.getId(),
      redeemVoucher.getVoucher().getId(),
      redeemVoucher.getUserId(),
      code,
      redeemVoucher.getStatus(),
      redeemVoucher.getRedeemedAt(),
      redeemVoucher.getExpiredAt()
    );
  }

  @Test
  public void testGetPoint_thenReturnPoint() throws Exception {
    UserPointTableSchema schema = UserPointTableSchema.newBuilder()
      .setUserId(UUID.randomUUID().toString())
      .setPoint(50)
      .build();

    when(voucherServiceStream.get(any())).thenReturn(schema);

    MvcResult result = this.mockMvc.perform(get("/api/v1/redeem/point/{id}", UUID.randomUUID()))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(schema.getPoint()));
  }
}
