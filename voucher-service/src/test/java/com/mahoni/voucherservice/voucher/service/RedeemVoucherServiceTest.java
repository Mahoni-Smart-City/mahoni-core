package com.mahoni.voucherservice.voucher.service;

import com.mahoni.schema.UserPointTableSchema;
import com.mahoni.voucherservice.merchant.model.Merchant;
import com.mahoni.voucherservice.merchant.model.MerchantRole;
import com.mahoni.voucherservice.voucher.dto.RedeemVoucherRequest;
import com.mahoni.voucherservice.voucher.dto.RedeemVoucherRequestCRUD;
import com.mahoni.voucherservice.voucher.exception.InsufficientUserPointException;
import com.mahoni.voucherservice.voucher.exception.RedeemVoucherNotFoundException;
import com.mahoni.voucherservice.voucher.kafka.VoucherEventProducer;
import com.mahoni.voucherservice.voucher.kafka.VoucherServiceStream;
import com.mahoni.voucherservice.voucher.model.RedeemVoucher;
import com.mahoni.voucherservice.voucher.model.Voucher;
import com.mahoni.voucherservice.voucher.model.VoucherStatus;
import com.mahoni.voucherservice.voucher.repository.RedeemVoucherRepository;
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
public class RedeemVoucherServiceTest {

  @Mock
  RedeemVoucherRepository redeemVoucherRepository;

  @Mock
  VoucherService voucherService;

  @Mock
  VoucherEventProducer voucherEventProducer;

  @Mock
  VoucherServiceStream voucherServiceStream;

  @InjectMocks
  RedeemVoucherService redeemVoucherService;

  @Captor
  ArgumentCaptor<RedeemVoucher> redeemVoucherArgumentCaptor;

  private UUID id;

  private Voucher voucher;

  private RedeemVoucher redeemVoucher;

  private RedeemVoucherRequestCRUD requestCRUD;

  private RedeemVoucherRequest request;

  @BeforeEach
  void init() {
    id = UUID.randomUUID();
    Merchant merchant = new Merchant();
    merchant.setUsername("Test");
    merchant.setRole(MerchantRole.MERCHANT);

    voucher = new Voucher();
    voucher.setId(id);
    voucher.setMerchant(merchant);
    voucher.setQuantity(0);
    voucher.setPoint(1);

    redeemVoucher = new RedeemVoucher();
    redeemVoucher.setVoucher(voucher);
    redeemVoucher.setRedeemCode("Test");
    redeemVoucher.setExpiredAt(LocalDateTime.now());
    requestCRUD = new RedeemVoucherRequestCRUD(id,"Test", LocalDateTime.now());
    request = new RedeemVoucherRequest(id, id);

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
  public void testGivenRedeemVoucherRequestCRUD_thenSaveRedeemVoucherMerchantRole() {
    when(voucherService.getById(any())).thenReturn(voucher);
    when(redeemVoucherRepository.save(any())).thenReturn(redeemVoucher);

    RedeemVoucher savedRedeemVoucher = redeemVoucherService.create(requestCRUD);

    assertEquals(savedRedeemVoucher, redeemVoucher);
    verify(redeemVoucherRepository).save(any());
  }

  @Test
  public void testGivenRedeemVoucherRequestCRUD_thenSaveRedeemVoucherAdminRole() {
    setAdmin();
    when(voucherService.getById(any())).thenReturn(voucher);
    when(redeemVoucherRepository.save(any())).thenReturn(redeemVoucher);

    RedeemVoucher savedRedeemVoucher = redeemVoucherService.create(requestCRUD);

    assertEquals(savedRedeemVoucher, redeemVoucher);
    verify(redeemVoucherRepository).save(any());
  }

  @Test
  public void testGivenRedeemVoucherRequestCRUD_thenThrowAccessDenied() {
    Authentication authentication = mock(Authentication.class);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    when(voucherService.getById(any())).thenReturn(voucher);
    when(authentication.getName()).thenReturn("Any");

    assertThrows(AccessDeniedException.class, () -> redeemVoucherService.create(requestCRUD));
  }

  @Test
  public void testGivenId_thenReturnRedeemVoucher() {
    when(redeemVoucherRepository.findById(any())).thenReturn(Optional.of(redeemVoucher));
    when(voucherService.getById(any())).thenReturn(voucher);
    RedeemVoucher savedRedeemVoucher = redeemVoucherService.getById(id);

    assertEquals(savedRedeemVoucher, redeemVoucher);
    verify(redeemVoucherRepository).findById(any());
  }

  @Test
  public void testGivenId_thenReturnRedeemVoucherNotFound() {
    when(redeemVoucherRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(RedeemVoucherNotFoundException.class, () -> redeemVoucherService.getById(id));
    assertThrows(RedeemVoucherNotFoundException.class, () -> redeemVoucherService.deleteById(id));
    assertThrows(RedeemVoucherNotFoundException.class, () -> redeemVoucherService.setRedeemed(id));
  }

  @Test
  public void testGivenId_thenReturnAccessDenied() {
    Authentication authentication = mock(Authentication.class);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    when(redeemVoucherRepository.findById(any())).thenReturn(Optional.of(redeemVoucher));
    when(voucherService.getById(any())).thenReturn(voucher);
    when(authentication.getName()).thenReturn("Any");

    assertThrows(AccessDeniedException.class, () -> redeemVoucherService.getById(id));
    assertThrows(AccessDeniedException.class, () -> redeemVoucherService.deleteById(id));
  }

  @Test
  public void testGetAllByAdmin_thenReturnRedeemVouchers() {
    setAdmin();
    List<RedeemVoucher> redeemVouchers = new ArrayList<>();
    redeemVouchers.add(redeemVoucher);

    when(redeemVoucherRepository.findAll()).thenReturn(redeemVouchers);
    List<RedeemVoucher> savedRedeemVouchers = redeemVoucherService.getAll();

    assertEquals(savedRedeemVouchers, redeemVouchers);
    verify(redeemVoucherRepository).findAll();
  }

  @Test
  public void testGetAllByMerchant_thenReturnRedeemVouchers() {
    List<RedeemVoucher> redeemVouchers = new ArrayList<>();
    redeemVouchers.add(redeemVoucher);

    when(redeemVoucherRepository.findAllByMerchant(any())).thenReturn(redeemVouchers);
    List<RedeemVoucher> savedRedeemVouchers = redeemVoucherService.getAll();

    assertEquals(savedRedeemVouchers, redeemVouchers);
    verify(redeemVoucherRepository).findAllByMerchant(any());
  }

  @Test
  public void testGetAllByUserId_thenReturnRedeemVouchers() {
    List<RedeemVoucher> redeemVouchers = new ArrayList<>();
    redeemVouchers.add(redeemVoucher);

    when(redeemVoucherRepository.findAllByUserId(any())).thenReturn(redeemVouchers);
    List<RedeemVoucher> savedRedeemVouchers = redeemVoucherService.getAllByUserId(id);

    assertEquals(savedRedeemVouchers, redeemVouchers);
    verify(redeemVoucherRepository).findAllByUserId(any());
  }

  @Test
  public void testGivenIdAndRedeemVoucherRequestCRUD_thenUpdateAndReturnUpdatedRedeemVoucherMerchantRole() {
    RedeemVoucher expectedRedeemVoucher = new RedeemVoucher(voucher, "Test2", LocalDateTime.now());
    requestCRUD = new RedeemVoucherRequestCRUD(id,"Test2", LocalDateTime.now());

    when(redeemVoucherRepository.findById(any())).thenReturn(Optional.of(redeemVoucher));
    when(voucherService.getById(any())).thenReturn(voucher);
    when(redeemVoucherRepository.save(any())).thenReturn(expectedRedeemVoucher);
    RedeemVoucher updatedRedeemVoucher = redeemVoucherService.update(id, requestCRUD);

    assertEquals(updatedRedeemVoucher, expectedRedeemVoucher);
    verify(redeemVoucherRepository).save(redeemVoucherArgumentCaptor.capture());
    assertEquals(redeemVoucherArgumentCaptor.getValue().getRedeemCode(), expectedRedeemVoucher.getRedeemCode());
  }

  @Test
  public void testGivenIdAndRedeemVoucherRequestCRUD_thenUpdatedAndReturnUpdatedRedeemVoucherAdminRole() {
    setAdmin();
    RedeemVoucher expectedRedeemVoucher = new RedeemVoucher(voucher, "Test2", LocalDateTime.now());
    requestCRUD = new RedeemVoucherRequestCRUD(id,"Test2", LocalDateTime.now());

    when(redeemVoucherRepository.findById(any())).thenReturn(Optional.of(redeemVoucher));
    when(voucherService.getById(any())).thenReturn(voucher);
    when(redeemVoucherRepository.save(any())).thenReturn(expectedRedeemVoucher);
    RedeemVoucher updatedRedeemVoucher = redeemVoucherService.update(id, requestCRUD);

    assertEquals(updatedRedeemVoucher, expectedRedeemVoucher);
    verify(redeemVoucherRepository).save(redeemVoucherArgumentCaptor.capture());
    assertEquals(redeemVoucherArgumentCaptor.getValue().getRedeemCode(), expectedRedeemVoucher.getRedeemCode());
  }

  @Test
  public void testGivenIdAndRedeemVoucherRequestCRUD_thenThrowRedeemVoucherNotFound() {
    when(redeemVoucherRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(RedeemVoucherNotFoundException.class, () -> redeemVoucherService.update(id, requestCRUD));
  }

  @Test
  public void testGivenIdAndRedeemVoucherRequestCRUD_thenThrowAccessDenied() {
    Authentication authentication = mock(Authentication.class);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    when(redeemVoucherRepository.findById(any())).thenReturn(Optional.of(redeemVoucher));
    when(voucherService.getById(any())).thenReturn(voucher);
    when(authentication.getName()).thenReturn("Any");

    assertThrows(AccessDeniedException.class, () -> redeemVoucherService.update(id, requestCRUD));
  }

  @Test
  public void testGivenIdToBeDeleted_thenDeleteAndReturnDeletedRedeemVoucherMerchantRole() {
    when(redeemVoucherRepository.findById(any())).thenReturn(Optional.of(redeemVoucher));
    when(voucherService.getById(any())).thenReturn(voucher);
    RedeemVoucher deletedRedeemVoucher = redeemVoucherService.deleteById(id);

    assertEquals(deletedRedeemVoucher, redeemVoucher);
    verify(redeemVoucherRepository).deleteById(any());
  }

  @Test
  public void testGivenIdToBeDeleted_thenDeleteAndReturnDeletedRedeemVoucherAdminRole() {
    setAdmin();
    when(redeemVoucherRepository.findById(any())).thenReturn(Optional.of(redeemVoucher));
    when(voucherService.getById(any())).thenReturn(voucher);
    RedeemVoucher deletedRedeemVoucher = redeemVoucherService.deleteById(id);

    assertEquals(deletedRedeemVoucher, redeemVoucher);
    verify(redeemVoucherRepository).deleteById(any());
  }

  @Test
  public void testGivenId_thenRedeemVoucherWithPendingStatus() {
    RedeemVoucher expectedRedeemVoucher = new RedeemVoucher(voucher, "Test", LocalDateTime.now());
    expectedRedeemVoucher.setStatus(VoucherStatus.PENDING);
    UserPointTableSchema userPointTableSchema = new UserPointTableSchema();
    userPointTableSchema.setPoint(2);

    when(redeemVoucherRepository.findAvailableRedeemVoucherByVoucherId(any())).thenReturn(Optional.of(redeemVoucher));
    when(voucherServiceStream.get(any())).thenReturn(userPointTableSchema);
    when(redeemVoucherRepository.save(any())).thenReturn(expectedRedeemVoucher);
    RedeemVoucher updatedRedeemVoucher = redeemVoucherService.redeem(request);

    assertEquals(updatedRedeemVoucher, expectedRedeemVoucher);
    verify(redeemVoucherRepository).save(redeemVoucherArgumentCaptor.capture());
    verify(voucherEventProducer).send(any());
    assertEquals(redeemVoucherArgumentCaptor.getValue().getStatus(), expectedRedeemVoucher.getStatus());
  }

  @Test
  public void testGivenId_thenThrowInsufficientUserPointException() {
    UserPointTableSchema userPointTableSchema = new UserPointTableSchema();
    userPointTableSchema.setPoint(0);

    when(redeemVoucherRepository.findAvailableRedeemVoucherByVoucherId(any())).thenReturn(Optional.of(redeemVoucher));
    when(voucherServiceStream.get(any())).thenReturn(userPointTableSchema);

    assertThrows(InsufficientUserPointException.class, () -> redeemVoucherService.redeem(request));
  }

  @Test
  public void testGivenId_thenThrowRedeemVoucherNotFound() {
    when(redeemVoucherRepository.findAvailableRedeemVoucherByVoucherId(any())).thenReturn(Optional.empty());

    assertThrows(RedeemVoucherNotFoundException.class, () -> redeemVoucherService.redeem(request));
  }

  @Test
  public void testGivenId_thenSetRedeemed() {
    RedeemVoucher expectedRedeemVoucher = new RedeemVoucher(voucher, "Test", LocalDateTime.now());
    expectedRedeemVoucher.setStatus(VoucherStatus.REDEEMED);

    when(redeemVoucherRepository.findById(any())).thenReturn(Optional.of(redeemVoucher));
    when(redeemVoucherRepository.save(any())).thenReturn(expectedRedeemVoucher);
    RedeemVoucher redeemedRedeemVoucher = redeemVoucherService.setRedeemed(id);

    assertEquals(redeemedRedeemVoucher, expectedRedeemVoucher);
    verify(redeemVoucherRepository).save(redeemVoucherArgumentCaptor.capture());
    assertEquals(redeemVoucherArgumentCaptor.getValue().getStatus(), expectedRedeemVoucher.getStatus());
  }

  @Test
  public void testScheduledCheck_thenUpdateStatus() {
    List<RedeemVoucher> redeemVouchers = new ArrayList<>();
    redeemVoucher.setStatus(VoucherStatus.ACTIVE);
    redeemVouchers.add(redeemVoucher);

    redeemVoucherRepository.save(redeemVoucher);
    when(redeemVoucherRepository.findAllByStatus(any())).thenReturn(redeemVouchers);
    redeemVoucherService.scheduledCheckAndUpdateStatus();

    assertEquals(redeemVoucher.getStatus(), VoucherStatus.EXPIRED);
  }

  @Test
  public void testScheduledCheckPending_thenResetExpiredPendingStatus() {
    List<RedeemVoucher> redeemVouchers = new ArrayList<>();
    redeemVoucher.setStatus(VoucherStatus.PENDING);
    redeemVoucher.setRedeemedAt(LocalDateTime.now().minusMinutes(10));
    redeemVouchers.add(redeemVoucher);

    redeemVoucherRepository.save(redeemVoucher);
    when(redeemVoucherRepository.findAllByStatus(VoucherStatus.PENDING)).thenReturn(redeemVouchers);
    redeemVoucherService.scheduledCheckAndResetPendingStatus();

    assertNull(redeemVoucher.getStatus());
    assertNull(redeemVoucher.getUserId());
    assertNull(redeemVoucher.getRedeemedAt());
  }
}
