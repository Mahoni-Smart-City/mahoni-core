package com.mahoni.voucherservice.merchant.service;

import com.mahoni.voucherservice.merchant.dto.MerchantRequest;
import com.mahoni.voucherservice.merchant.model.MerchantRole;
import com.mahoni.voucherservice.merchant.exception.MerchantAlreadyExistException;
import com.mahoni.voucherservice.merchant.exception.MerchantNotFoundException;
import com.mahoni.voucherservice.merchant.model.Merchant;
import com.mahoni.voucherservice.merchant.repository.MerchantRepository;
import org.junit.jupiter.api.BeforeEach;
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

  private Merchant merchant;
  private MerchantRequest request;

  @BeforeEach
  void init() {
    request = new MerchantRequest("Test", "Test", "Test@mail.com", "Test");
    merchant = new Merchant(UUID.randomUUID(), "Test", "Test", "Test@mail.com", "Test", MerchantRole.MERCHANT);
    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(merchant, null, merchant.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(auth);
  }

  void setAdmin() {
    Merchant admin = new Merchant();
    admin.setRole(MerchantRole.ADMIN);
    admin.setUsername("Admin");
    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(admin, null, admin.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(auth);
  }

  @Test
  public void testGivenMerchantRequest_thenSaveMerchant() {
    when(merchantRepository.findByUsername(any())).thenReturn(Optional.empty());
    when(merchantRepository.save(any())).thenReturn(merchant);
    when(passwordEncoder.encode(any())).thenReturn(any(String.class));
    Merchant savedMerchant = merchantService.create(request);

    assertEquals(savedMerchant, merchant);
    assertEquals(savedMerchant.getId(), merchant.getId());
    assertEquals(savedMerchant.getUsername(), merchant.getUsername());
    assertEquals(savedMerchant.getName(), merchant.getName());
    assertEquals(savedMerchant.getEmail(), merchant.getEmail());
    assertEquals(savedMerchant.getPassword(), merchant.getPassword());
    assertEquals(savedMerchant.getRole(), merchant.getRole());
    assertTrue(savedMerchant.isAccountNonExpired());
    assertTrue(savedMerchant.isAccountNonLocked());
    assertTrue(savedMerchant.isCredentialsNonExpired());
    assertTrue(savedMerchant.isEnabled());
    verify(merchantRepository).save(any());
    verify(passwordEncoder).encode(any());
  }

  @Test
  public void testGivenMerchantRequest_thenThrowMerchantAlreadyExist() {
    when(merchantRepository.findByUsername(any())).thenReturn(Optional.of(merchant));

    assertThrows(MerchantAlreadyExistException.class, () -> merchantService.create(request));
  }

  @Test
  public void testGivenId_thenReturnMerchant() {
    when(merchantRepository.findById(any())).thenReturn(Optional.of(merchant));
    Merchant savedMerchant = merchantService.getById(UUID.randomUUID());

    assertEquals(savedMerchant, merchant);
    verify(merchantRepository).findById(any());
  }

  @Test
  public void testGivenId_thenThrowMerchantNotFound() {
    when(merchantRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(MerchantNotFoundException.class, () -> merchantService.getById(UUID.randomUUID()));
  }

  @Test
  public void testGivenUsername_thenReturnMerchant() {
    when(merchantRepository.findByUsername(any())).thenReturn(Optional.of(merchant));
    Merchant savedMerchant = merchantService.getByUsername("Test");

    assertEquals(savedMerchant, merchant);
    verify(merchantRepository).findByUsername(any());
  }

  @Test
  public void testGivenUsername_thenThrowMerchantNotFound() {
    when(merchantRepository.findByUsername(any())).thenReturn(Optional.empty());

    assertThrows(MerchantNotFoundException.class, () -> merchantService.getByUsername("Test"));
  }

  @Test
  public void testGetAll_thenReturnMerchantsOnly() {
    List<Merchant> merchants = new ArrayList<>();
    merchants.add(merchant);

    when(merchantRepository.findAllByRole(MerchantRole.MERCHANT)).thenReturn(merchants);
    List<Merchant> savedMerchants = merchantService.getAll();

    assertEquals(savedMerchants, merchants);
    verify(merchantRepository).findAllByRole(any());
  }

  @Test
  public void testGetAll_thenReturnMerchantsAndAdmin() {
    setAdmin();
    List<Merchant> merchants = new ArrayList<>();
    merchant.setRole(MerchantRole.ADMIN);
    merchants.add(merchant);

    when(merchantRepository.findAll()).thenReturn(merchants);
    List<Merchant> savedMerchants = merchantService.getAll();

    assertEquals(savedMerchants, merchants);
    verify(merchantRepository).findAll();
  }

  @Test
  public void testGivenIdToBeDeleted_thenDeleteAndReturnDeletedMerchantWithMerchantRole() {
    when(merchantRepository.findById(any())).thenReturn(Optional.of(merchant));
    Merchant deletedMerchant = merchantService.deleteById(UUID.randomUUID());

    assertEquals(deletedMerchant, merchant);
    verify(merchantRepository).deleteById(any());
  }

  @Test
  public void testGivenIdToBeDeleted_thenDeleteAndReturnDeletedMerchantWithAdminRole() {
    setAdmin();
    when(merchantRepository.findById(any())).thenReturn(Optional.of(merchant));
    Merchant deletedMerchant = merchantService.deleteById(UUID.randomUUID());

    assertEquals(deletedMerchant, merchant);
    verify(merchantRepository).deleteById(any());
  }

  @Test
  public void testGivenIdToBeDeleted_thenThrowMerchantNotFound() {
    when(merchantRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(MerchantNotFoundException.class, () -> merchantService.deleteById(UUID.randomUUID()));
  }

  @Test
  public void testGivenIdToBeDeleted_thenThrowAccessDenied() {
    Authentication authentication = mock(Authentication.class);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    when(merchantRepository.findById(any())).thenReturn(Optional.of(merchant));
    when(authentication.getName()).thenReturn("Any");

    assertThrows(AccessDeniedException.class ,() -> merchantService.deleteById(UUID.randomUUID()));
  }

  @Test
  public void testGivenIdAndMerchantRequest_thenUpdateAndReturnUpdatedMerchantWithMerchantRole() {
    request = new MerchantRequest("Test2", "Test", "Test@mail.com", "Test");
    Merchant expectedMerchant = new Merchant("Test2", "Test", "Test@mail.com", "Test", MerchantRole.MERCHANT);

    when(merchantRepository.findById(any())).thenReturn(Optional.of(merchant));
    when(merchantRepository.save(any())).thenReturn(expectedMerchant);
    when(passwordEncoder.encode(any())).thenReturn(any(String.class));
    Merchant updatedMerchant = merchantService.update(UUID.randomUUID(), request);

    assertEquals(updatedMerchant, expectedMerchant);
    verify(merchantRepository).save(merchantArgumentCaptor.capture());
    assertEquals(merchantArgumentCaptor.getValue().getUsername(), expectedMerchant.getUsername());
  }

  @Test
  public void testGivenIdAndMerchantRequest_thenUpdateAndReturnUpdatedMerchantWithAdminRole() {
    setAdmin();
    request = new MerchantRequest("Test2", "Test", "Test@mail.com", "Test");
    Merchant expectedMerchant = new Merchant("Test2", "Test", "Test@mail.com", "Test", MerchantRole.MERCHANT);

    when(merchantRepository.findById(any())).thenReturn(Optional.of(merchant));
    when(merchantRepository.save(any())).thenReturn(expectedMerchant);
    when(passwordEncoder.encode(any())).thenReturn(any(String.class));
    Merchant updatedMerchant = merchantService.update(UUID.randomUUID(), request);

    assertEquals(updatedMerchant, expectedMerchant);
    verify(merchantRepository).save(merchantArgumentCaptor.capture());
    assertEquals(merchantArgumentCaptor.getValue().getUsername(), expectedMerchant.getUsername());
  }

  @Test
  public void testGivenIdAndMerchantRequest_thenThrowMerchantNotFound() {
    when(merchantRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(MerchantNotFoundException.class, () -> merchantService.update(UUID.randomUUID(), request));
  }

  @Test
  public void testGivenIdAndMerchantRequest_thenThrowAccessDenied() {
    Authentication authentication = mock(Authentication.class);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    when(merchantRepository.findById(any())).thenReturn(Optional.of(merchant));
    when(authentication.getName()).thenReturn("Any");

    assertThrows(AccessDeniedException.class, () -> merchantService.update(UUID.randomUUID(), request));
  }
}
