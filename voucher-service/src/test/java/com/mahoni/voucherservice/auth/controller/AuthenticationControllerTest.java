package com.mahoni.voucherservice.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mahoni.voucherservice.auth.config.JwtTokenUtil;
import com.mahoni.voucherservice.auth.dto.AuthenticationRequest;
import com.mahoni.voucherservice.auth.dto.AuthenticationResponse;
import com.mahoni.voucherservice.auth.service.AuthenticationService;
import com.mahoni.voucherservice.merchant.dto.MerchantRequest;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@Import(JwtTokenUtil.class)
@WebMvcTest(AuthenticationController.class)
public class AuthenticationControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private WebApplicationContext context;

  @MockBean
  private AuthenticationService authenticationService;

  private AuthenticationResponse response;

  @BeforeEach
  void init() {
    response = new AuthenticationResponse();
    response.setToken("Test");
    mockMvc = MockMvcBuilders
      .webAppContextSetup(context)
      .build();
  }

  @Test
  public void testRegister_thenReturnAuthenticationResponse() throws Exception {
    MerchantRequest merchantRequest = new MerchantRequest("Test", "Test", "Test@mail.com", "Test");

    when(authenticationService.register(any())).thenReturn(response);

    MvcResult result = this.mockMvc.perform(post("/api/v1/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(merchantRequest)))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(response));
    verify(authenticationService).register(any());
  }

  @Test
  public void testAuthenticate_thenReturnAuthenticationResponse() throws Exception {
    AuthenticationRequest authenticationRequest = new AuthenticationRequest("Test", "Test");

    when(authenticationService.authenticate(any())).thenReturn(response);

    MvcResult result = this.mockMvc.perform(post("/api/v1/auth/authenticate")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(authenticationRequest)))
      .andExpect(status().isOk())
      .andReturn();

    assertEquals(result.getResponse().getContentAsString(), objectMapper.writeValueAsString(response));
    verify(authenticationService).authenticate(any());
  }
}
