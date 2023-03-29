package com.mahoni.voucherservice.auth.service;

import com.mahoni.voucherservice.auth.config.JwtTokenUtil;
import com.mahoni.voucherservice.auth.dto.AuthenticationRequest;
import com.mahoni.voucherservice.auth.dto.AuthenticationResponse;
import com.mahoni.voucherservice.merchant.dto.MerchantRequest;
import com.mahoni.voucherservice.merchant.model.MerchantRole;
import com.mahoni.voucherservice.merchant.exception.MerchantAlreadyExistException;
import com.mahoni.voucherservice.merchant.exception.MerchantNotFoundException;
import com.mahoni.voucherservice.merchant.model.Merchant;
import com.mahoni.voucherservice.merchant.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

  private final MerchantRepository merchantRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenUtil jwtTokenUtil;
  private final AuthenticationManager authenticationManager;

  public AuthenticationResponse register(MerchantRequest request) {
    if (merchantRepository.findByUsername(request.getUsername()).isPresent()) {
      throw new MerchantAlreadyExistException(request.getUsername());
    }

    Merchant merchant = new Merchant(
      request.getUsername(),
      request.getName(),
      request.getEmail(),
      passwordEncoder.encode(request.getPassword()),
      MerchantRole.MERCHANT
    );

    merchantRepository.save(merchant);
    String jwtToken = jwtTokenUtil.generateToken(merchant);

    return new AuthenticationResponse(jwtToken);
  }

  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(
        request.getUsername(),
        request.getPassword()
      )
    );

    Merchant merchant = merchantRepository.findByUsername(request.getUsername())
      .orElseThrow(() -> new MerchantNotFoundException(request.getUsername()));
    String jwtToken = jwtTokenUtil.generateToken(merchant);

    return new AuthenticationResponse(jwtToken);
  }
}
