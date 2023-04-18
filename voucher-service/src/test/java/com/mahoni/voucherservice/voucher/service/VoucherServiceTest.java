package com.mahoni.voucherservice.voucher.service;

import com.mahoni.voucherservice.merchant.model.Merchant;
import com.mahoni.voucherservice.merchant.model.MerchantRole;
import com.mahoni.voucherservice.merchant.service.MerchantService;
import com.mahoni.voucherservice.voucher.dto.VoucherRequest;
import com.mahoni.voucherservice.voucher.exception.VoucherNotFoundException;
import com.mahoni.voucherservice.voucher.model.Voucher;
import com.mahoni.voucherservice.voucher.model.VoucherType;
import com.mahoni.voucherservice.voucher.repository.VoucherRepository;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VoucherServiceTest {

  @Mock
  VoucherRepository voucherRepository;

  @Mock
  MerchantService merchantService;

  @InjectMocks
  VoucherService voucherService;

  @Captor
  ArgumentCaptor<Voucher> voucherArgumentCaptor;

  private Merchant merchant;
  private VoucherRequest request;
  private Voucher voucher;
  private UUID id;

  @BeforeEach
  void init() {
    id = UUID.randomUUID();
    merchant = new Merchant("Test", "Test", "Test@mail.com", "Test", MerchantRole.MERCHANT);
    request = new VoucherRequest("Test", "Test", VoucherType.FNB, 1, LocalDateTime.now(), LocalDateTime.now());
    voucher = new Voucher("Test", "Test", VoucherType.FNB, 1, LocalDateTime.now(), LocalDateTime.now(), merchant);
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
  public void testGivenVoucherRequest_thenSaveVoucher() {
    when(merchantService.getByUsername(any())).thenReturn(merchant);
    when(voucherRepository.save(any())).thenReturn(voucher);
    Voucher savedVoucher = voucherService.create(request);

    assertEquals(savedVoucher, voucher);
    assertEquals(savedVoucher.getId(), voucher.getId());
    assertEquals(savedVoucher.getName(), voucher.getName());
    assertEquals(savedVoucher.getDescription(), voucher.getDescription());
    assertEquals(savedVoucher.getType(), voucher.getType());
    assertEquals(savedVoucher.getPoint(), voucher.getPoint());
    assertEquals(savedVoucher.getStartAt(), voucher.getStartAt());
    assertEquals(savedVoucher.getExpiredAt(), voucher.getExpiredAt());
    assertEquals(savedVoucher.getMerchant(), voucher.getMerchant());
    assertEquals(savedVoucher.getQuantity(), voucher.getQuantity());
    verify(voucherRepository).save(any());
  }

  @Test
  public void testGivenId_thenReturnVoucher() {
    when(voucherRepository.findById(any())).thenReturn(Optional.of(voucher));
    Voucher savedVoucher = voucherService.getById(id);

    assertEquals(savedVoucher, voucher);
    verify(voucherRepository).findById(any());
  }

  @Test
  public void testGivenId_thenThrowVoucherNotFound() {
    when(voucherRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(VoucherNotFoundException.class, () -> voucherService.getById(id));
  }

  @Test
  public void testGetAll_thenReturnVouchers() {
    List<Voucher> vouchers = new ArrayList<>();
    vouchers.add(voucher);

    when(voucherRepository.findAll()).thenReturn(vouchers);
    List<Voucher> savedVouchers = voucherService.getAll();

    assertEquals(savedVouchers, vouchers);
    verify(voucherRepository).findAll();
  }

  @Test
  public void testGivenIdToBeDeleted_thenDeleteAndReturnDeletedVoucherMerchantRole() {
    when(voucherRepository.findById(any())).thenReturn(Optional.of(voucher));
    Voucher deletedVoucher = voucherService.deleteById(id);

    assertEquals(deletedVoucher, voucher);
    verify(voucherRepository).deleteById(any());
  }

  @Test
  public void testGivenIdToBeDeleted_thenDeleteAndReturnDeletedVoucherAdminRole() {
    setAdmin();
    when(voucherRepository.findById(any())).thenReturn(Optional.of(voucher));
    Voucher deletedVoucher = voucherService.deleteById(id);

    assertEquals(deletedVoucher, voucher);
    verify(voucherRepository).deleteById(any());
  }

  @Test
  public void testGivenIdToBeDeleted_thenThrowVoucherNotFound() {
    when(voucherRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(VoucherNotFoundException.class, () -> voucherService.deleteById(id));
  }

  @Test
  public void testGivenIdToBeDeleted_thenThrowAccessDenied() {
    Authentication authentication = mock(Authentication.class);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    when(voucherRepository.findById(any())).thenReturn(Optional.of(voucher));
    when(authentication.getName()).thenReturn("Any");

    assertThrows(AccessDeniedException.class, () -> voucherService.deleteById(id));
  }

  @Test
  public void testGivenIdAndVoucherRequest_thenUpdateAndReturnUpdatedVoucherMerchantRole() {
    request = new VoucherRequest("Test2", "Test", VoucherType.FNB, 1, LocalDateTime.now(), LocalDateTime.now());
    Voucher expectedVoucher = new Voucher("Test2", "Test", VoucherType.FNB, 1, LocalDateTime.now(), LocalDateTime.now(), merchant);

    when(voucherRepository.findById(any())).thenReturn(Optional.of(voucher));
    when(voucherRepository.save(any())).thenReturn(expectedVoucher);
    Voucher updatedVoucher = voucherService.update(id, request);

    assertEquals(updatedVoucher, expectedVoucher);
    verify(voucherRepository).save(voucherArgumentCaptor.capture());
    assertEquals(voucherArgumentCaptor.getValue().getName(), expectedVoucher.getName());
  }

  @Test
  public void testGivenIdAndVoucherRequest_thenUpdateAndReturnUpdatedVoucherAdminRole() {
    setAdmin();
    request = new VoucherRequest("Test2", "Test", VoucherType.FNB, 1, LocalDateTime.now(), LocalDateTime.now());
    Voucher expectedVoucher = new Voucher("Test2", "Test", VoucherType.FNB, 1, LocalDateTime.now(), LocalDateTime.now(), merchant);

    when(voucherRepository.findById(any())).thenReturn(Optional.of(voucher));
    when(voucherRepository.save(any())).thenReturn(expectedVoucher);
    Voucher updatedVoucher = voucherService.update(id, request);

    assertEquals(updatedVoucher, expectedVoucher);
    verify(voucherRepository).save(voucherArgumentCaptor.capture());
    assertEquals(voucherArgumentCaptor.getValue().getName(), expectedVoucher.getName());
  }

  @Test
  public void testGivenIdAndVoucherRequest_thenThrowVoucherNotFound() {
    when(voucherRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(VoucherNotFoundException.class, () -> voucherService.update(id, request));
  }

  @Test
  public void testGivenIdAndVoucherRequest_thenThrowAccessDenied() {
    Authentication authentication = mock(Authentication.class);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    when(voucherRepository.findById(any())).thenReturn(Optional.of(voucher));
    when(authentication.getName()).thenReturn("Any");

    assertThrows(AccessDeniedException.class, () -> voucherService.update(id, request));
  }
}
