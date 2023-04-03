package com.mahoni.voucherservice.merchant.service;

import com.mahoni.voucherservice.merchant.dto.MerchantRequest;
import com.mahoni.voucherservice.merchant.model.MerchantRole;
import com.mahoni.voucherservice.merchant.exception.MerchantAlreadyExistException;
import com.mahoni.voucherservice.merchant.exception.MerchantNotFoundException;
import com.mahoni.voucherservice.merchant.model.Merchant;
import com.mahoni.voucherservice.merchant.repository.MerchantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MerchantServiceTest {

  @Mock
  MerchantRepository merchantRepository;

  @Mock
  PasswordEncoder passwordEncoder;

  @InjectMocks
  MerchantService merchantService;

  @Captor
  ArgumentCaptor<Merchant> merchantArgumentCaptor;

  @Test
  public void testGivenMerchantRequest_thenSaveMerchant() {
    MerchantRequest request = new MerchantRequest("Test", "Test", "Test@mail.com", "Test");
    Merchant merchant = new Merchant("Test", "Test", "Test@mail.com", "Test", MerchantRole.MERCHANT);

    when(merchantRepository.findByUsername(any())).thenReturn(Optional.empty());
    when(merchantRepository.save(any())).thenReturn(merchant);
    when(passwordEncoder.encode(any())).thenReturn(any(String.class));
    Merchant savedMerchant = merchantService.create(request);

    assertEquals(savedMerchant, merchant);
    verify(merchantRepository).save(any());
    verify(passwordEncoder).encode(any());
  }

  @Test
  public void testGivenMerchantRequest_thenThrowMerchantAlreadyExist() {
    MerchantRequest request = new MerchantRequest("Test", "Test", "Test@mail.com", "Test");
    Merchant merchant = new Merchant("Test", "Test", "Test@mail.com", "Test", MerchantRole.MERCHANT);

    when(merchantRepository.findByUsername(any())).thenReturn(Optional.of(merchant));

    assertThrows(MerchantAlreadyExistException.class, () -> merchantService.create(request));
  }

  @Test
  public void testGivenId_thenReturnMerchant() {
    UUID id = UUID.randomUUID();
    Merchant merchant = new Merchant("Test", "Test", "Test@mail.com", "Test", MerchantRole.MERCHANT);

    when(merchantRepository.findById(any())).thenReturn(Optional.of(merchant));
    Merchant savedMerchant = merchantService.getById(id);

    assertEquals(savedMerchant, merchant);
    verify(merchantRepository).findById(any());
  }

  @Test
  public void testGivenId_thenThrowMerchantNotFound() {
    UUID id = UUID.randomUUID();

    when(merchantRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(MerchantNotFoundException.class, () -> merchantService.getById(id));
  }

  @Test
  public void testGivenUsername_thenReturnMerchant() {
    String username = "Test";
    Merchant merchant = new Merchant("Test", "Test", "Test@mail.com", "Test", MerchantRole.MERCHANT);

    when(merchantRepository.findByUsername(any())).thenReturn(Optional.of(merchant));
    Merchant savedMerchant = merchantService.getByUsername(username);

    assertEquals(savedMerchant, merchant);
    verify(merchantRepository).findByUsername(any());
  }

  @Test
  public void testGivenUsername_thenThrowMerchantNotFound() {
    String username = "Test";

    when(merchantRepository.findByUsername(any())).thenReturn(Optional.empty());

    assertThrows(MerchantNotFoundException.class, () -> merchantService.getByUsername(username));
  }

  @Test
  public void testGetAll_thenReturnMerchants() {
    Merchant merchant = new Merchant("Test", "Test", "Test@mail.com", "Test", MerchantRole.MERCHANT);
    List<Merchant> merchants = new ArrayList<>();
    merchants.add(merchant);

    when(merchantRepository.findAll()).thenReturn(merchants);
    List<Merchant> savedMerchants = merchantService.getAll();

    assertEquals(savedMerchants, merchants);
    verify(merchantRepository).findAll();
  }

  @Test
  public void testGivenIdToBeDeleted_thenDeleteAndReturnDeletedMerchantWithMerchantRole() {
    UUID id = UUID.randomUUID();
    Merchant merchant = new Merchant("Test", "Test", "Test@mail.com", "Test", MerchantRole.MERCHANT);
    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(merchant, null, merchant.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(auth);

    when(merchantRepository.findById(any())).thenReturn(Optional.of(merchant));
    Merchant deletedMerchant = merchantService.deleteById(id);

    assertEquals(deletedMerchant, merchant);
    verify(merchantRepository).deleteById(any());
  }

  @Test
  public void testGivenIdToBeDeleted_thenDeleteAndReturnDeletedMerchantWithAdminRole() {
    UUID id = UUID.randomUUID();
    Merchant merchant = new Merchant("Test", "Test", "Test@mail.com", "Test", MerchantRole.MERCHANT);
    Merchant admin = new Merchant("Test2", "Test", "Test@mail.com", "Test", MerchantRole.ADMIN);
    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(admin, null, admin.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(auth);

    when(merchantRepository.findById(any())).thenReturn(Optional.of(merchant));
    Merchant deletedMerchant = merchantService.deleteById(id);

    assertEquals(deletedMerchant, merchant);
    verify(merchantRepository).deleteById(any());
  }

  @Test
  public void testGivenIdToBeDeleted_thenThrowMerchantNotFound() {
    UUID id = UUID.randomUUID();

    when(merchantRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(MerchantNotFoundException.class, () -> merchantService.deleteById(id));
  }

  @Test
  public void testGivenIdToBeDeleted_thenThrowAccessDenied() {
    UUID id = UUID.randomUUID();
    Merchant merchant = new Merchant("Test2", "Test", "Test@mail.com", "Test", MerchantRole.MERCHANT);
    Authentication authentication = mock(Authentication.class);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    when(merchantRepository.findById(any())).thenReturn(Optional.of(merchant));

    assertThrows(AccessDeniedException.class ,() -> merchantService.deleteById(id));
  }

  @Test
  public void testGivenIdAndMerchantRequest_thenUpdateAndReturnUpdatedMerchantWithMerchantRole() {
    UUID id = UUID.randomUUID();
    MerchantRequest request = new MerchantRequest("Test2", "Test", "Test@mail.com", "Test");
    Merchant merchant = new Merchant("Test", "Test", "Test@mail.com", "Test", MerchantRole.MERCHANT);
    Merchant expectedMerchant = new Merchant("Test2", "Test", "Test@mail.com", "Test", MerchantRole.MERCHANT);
    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(merchant, null, merchant.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(auth);

    when(merchantRepository.findById(any())).thenReturn(Optional.of(merchant));
    when(merchantRepository.save(any())).thenReturn(expectedMerchant);
    when(passwordEncoder.encode(any())).thenReturn(any(String.class));
    Merchant updatedMerchant = merchantService.update(id, request);

    assertEquals(updatedMerchant, expectedMerchant);
    verify(merchantRepository).save(merchantArgumentCaptor.capture());
    assertEquals(merchantArgumentCaptor.getValue().getUsername(), expectedMerchant.getUsername());
  }

  @Test
  public void testGivenIdAndMerchantRequest_thenUpdateAndReturnUpdatedMerchantWithAdminRole() {
    UUID id = UUID.randomUUID();
    MerchantRequest request = new MerchantRequest("Test2", "Test", "Test@mail.com", "Test");
    Merchant merchant = new Merchant("Test", "Test", "Test@mail.com", "Test", MerchantRole.MERCHANT);
    Merchant expectedMerchant = new Merchant("Test2", "Test", "Test@mail.com", "Test", MerchantRole.MERCHANT);
    Merchant admin = new Merchant("Test2", "Test", "Test@mail.com", "Test", MerchantRole.ADMIN);
    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(admin, null, admin.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(auth);

    when(merchantRepository.findById(any())).thenReturn(Optional.of(merchant));
    when(merchantRepository.save(any())).thenReturn(expectedMerchant);
    when(passwordEncoder.encode(any())).thenReturn(any(String.class));
    Merchant updatedMerchant = merchantService.update(id, request);

    assertEquals(updatedMerchant, expectedMerchant);
    verify(merchantRepository).save(merchantArgumentCaptor.capture());
    assertEquals(merchantArgumentCaptor.getValue().getUsername(), expectedMerchant.getUsername());
  }

  @Test
  public void testGivenIdAndMerchantRequest_thenThrowMerchantNotFound() {
    UUID id = UUID.randomUUID();
    MerchantRequest request = new MerchantRequest("Test2", "Test", "Test@mail.com", "Test");

    when(merchantRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(MerchantNotFoundException.class, () -> merchantService.update(id, request));
  }

  @Test
  public void testGivenIdAndMerchantRequest_thenThrowAccessDenied() {
    UUID id = UUID.randomUUID();
    MerchantRequest request = new MerchantRequest("Test2", "Test", "Test@mail.com", "Test");
    Merchant merchant = new Merchant("Test", "Test", "Test@mail.com", "Test", MerchantRole.MERCHANT);
    Authentication authentication = mock(Authentication.class);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    when(merchantRepository.findById(any())).thenReturn(Optional.of(merchant));

    assertThrows(AccessDeniedException.class, () -> merchantService.update(id, request));
  }
}
