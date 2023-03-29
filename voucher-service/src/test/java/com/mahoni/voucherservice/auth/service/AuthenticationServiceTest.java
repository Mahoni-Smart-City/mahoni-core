package com.mahoni.voucherservice.auth.service;

import com.mahoni.voucherservice.auth.config.JwtTokenUtil;
import com.mahoni.voucherservice.auth.dto.AuthenticationRequest;
import com.mahoni.voucherservice.auth.dto.AuthenticationResponse;
import com.mahoni.voucherservice.merchant.dto.MerchantRequest;
import com.mahoni.voucherservice.merchant.exception.MerchantAlreadyExistException;
import com.mahoni.voucherservice.merchant.exception.MerchantNotFoundException;
import com.mahoni.voucherservice.merchant.model.Merchant;
import com.mahoni.voucherservice.merchant.model.MerchantRole;
import com.mahoni.voucherservice.merchant.repository.MerchantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

  @Mock
  MerchantRepository merchantRepository;

  @Mock
  PasswordEncoder passwordEncoder;

  @Mock
  AuthenticationManager authenticationManager;

  @Mock
  JwtTokenUtil jwtTokenUtil;

  @InjectMocks
  AuthenticationService authenticationService;

  @Test
  public void testGivenMerchantRequest_thenRegisterMerchant() {
    MerchantRequest request = new MerchantRequest("Test", "Test", "Test@mail.com", "Test");
    Merchant merchant = new Merchant("Test", "Test", "Test@mail.com", "Test", MerchantRole.MERCHANT);
    String token = "Test";
    AuthenticationResponse response = new AuthenticationResponse("Test");

    when(merchantRepository.findByUsername(any())).thenReturn(Optional.empty());
    when(passwordEncoder.encode(any())).thenReturn("Test");
    when(merchantRepository.save(any())).thenReturn(merchant);
    when(jwtTokenUtil.generateToken(any())).thenReturn(token);

    AuthenticationResponse createdResponse = authenticationService.register(request);

    assertEquals(createdResponse, response);
    verify(merchantRepository).save(any());
    verify(jwtTokenUtil).generateToken(any());
  }

  @Test
  public void testGivenMerchantRequest_thenThrowMerchantAlreadyExist() {
    MerchantRequest request = new MerchantRequest("Test", "Test", "Test@mail.com", "Test");
    Merchant merchant = new Merchant("Test", "Test", "Test@mail.com", "Test", MerchantRole.MERCHANT);

    when(merchantRepository.findByUsername(any())).thenReturn(Optional.of(merchant));

    assertThrows(MerchantAlreadyExistException.class, () -> authenticationService.register(request));
  }

  @Test
  public void testGivenAuthenticationRequest_thenAuthenticateMerchant() {
    AuthenticationRequest request = new AuthenticationRequest("Test", "Test");
    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("Test", "Test");
    Merchant merchant = new Merchant("Test", "Test", "Test@mail.com", "Test", MerchantRole.MERCHANT);
    String token = "Test";
    AuthenticationResponse response = new AuthenticationResponse("Test");

    when(authenticationManager.authenticate(any())).thenReturn(auth);
    when(merchantRepository.findByUsername(any())).thenReturn(Optional.of(merchant));
    when(jwtTokenUtil.generateToken(any())).thenReturn(token);

    AuthenticationResponse createdResponse = authenticationService.authenticate(request);
    assertEquals(createdResponse, response);
    verify(merchantRepository).findByUsername(any());
    verify(jwtTokenUtil).generateToken(any());
  }

  @Test
  public void testGivenAuthenticationRequest_thenThrowMerchantNotFound() {
    AuthenticationRequest request = new AuthenticationRequest("Test", "Test");
    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("Test", "Test");

    when(authenticationManager.authenticate(any())).thenReturn(auth);
    when(merchantRepository.findByUsername(any())).thenReturn(Optional.empty());

    assertThrows(MerchantNotFoundException.class, () -> authenticationService.authenticate(request));
  }
}
