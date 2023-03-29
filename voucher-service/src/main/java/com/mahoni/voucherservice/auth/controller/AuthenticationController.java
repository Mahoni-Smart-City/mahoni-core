package com.mahoni.voucherservice.auth.controller;

import com.mahoni.voucherservice.auth.dto.AuthenticationRequest;
import com.mahoni.voucherservice.auth.dto.AuthenticationResponse;
import com.mahoni.voucherservice.auth.service.AuthenticationService;
import com.mahoni.voucherservice.merchant.dto.MerchantRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthenticationController {

  @Autowired
  AuthenticationService authenticationService;

  @PostMapping("/register")
  public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody MerchantRequest request) {
    return ResponseEntity.ok(authenticationService.register(request));
  }

  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate(@Valid @RequestBody AuthenticationRequest request) {
    return ResponseEntity.ok(authenticationService.authenticate(request));
  }
}
