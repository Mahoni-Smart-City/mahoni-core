package com.mahoni.voucherservice.service;

import com.mahoni.voucherservice.dto.VoucherRequest;
import com.mahoni.voucherservice.exception.VoucherAlreadyExistException;
import com.mahoni.voucherservice.exception.VoucherNotFoundException;
import com.mahoni.voucherservice.model.Voucher;
import com.mahoni.voucherservice.repository.VoucherRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VoucherServiceTest {

  @Mock
  VoucherRepository voucherRepository;

  @InjectMocks
  VoucherService voucherService;

  @Captor
  ArgumentCaptor<Voucher> voucherArgumentCaptor;

  @Test
  public void testGivenVoucherRequest_thenSaveVoucher() throws Exception {
    VoucherRequest request = new VoucherRequest("Test", "Test", "Test", LocalDateTime.now(), LocalDateTime.now());
    Voucher voucher = new Voucher("Test", "Test", "Test", LocalDateTime.now(), LocalDateTime.now());

    when(voucherRepository.findByName(any())).thenReturn(Optional.empty());
    when(voucherRepository.save(any())).thenReturn(voucher);
    Voucher savedVoucher = voucherService.create(request);

    assertEquals(savedVoucher, voucher);
    verify(voucherRepository).save(any());
  }

  @Test
  public void testGivenVoucherRequest_thenThrowVoucherAlreadyExist() throws Exception {
    VoucherRequest request = new VoucherRequest("Test", "Test", "Test", LocalDateTime.now(), LocalDateTime.now());
    Voucher voucher = new Voucher("Test", "Test", "Test", LocalDateTime.now(), LocalDateTime.now());

    when(voucherRepository.findByName(any())).thenReturn(Optional.of(voucher));
    when(voucherRepository.findByCode(any())).thenReturn(Optional.of(voucher));

    assertThrows(VoucherAlreadyExistException.class, () -> {
      voucherService.create(request);
    });
  }

  @Test
  public void testGivenId_thenReturnVoucher() throws Exception {
    Long id = 1L;
    Voucher voucher = new Voucher("Test", "Test", "Test", LocalDateTime.now(), LocalDateTime.now());

    when(voucherRepository.findById(any())).thenReturn(Optional.of(voucher));
    Voucher savedVoucher = voucherService.getById(id);

    assertEquals(savedVoucher, voucher);
    verify(voucherRepository).findById(any());
  }

  @Test
  public void testGivenId_thenThrowVoucherNotFound() throws Exception {
    Long id = 1L;

    when(voucherRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(VoucherNotFoundException.class, () -> {
      voucherService.getById(id);
    });
  }

  @Test
  public void testGetAll_thenReturnVouchers() throws Exception {
    Voucher voucher = new Voucher("Test", "Test", "Test", LocalDateTime.now(), LocalDateTime.now());
    List<Voucher> vouchers = new ArrayList<>();
    vouchers.add(voucher);

    when(voucherRepository.findAll()).thenReturn(vouchers);
    List<Voucher> savedVouchers = voucherService.getAll();

    assertEquals(savedVouchers, vouchers);
    verify(voucherRepository).findAll();
  }

  @Test
  public void testGivenIdToBeDeleted_thenDeleteAndReturnDeletedVoucher() throws Exception {
    Long id = 1L;
    Voucher voucher = new Voucher("Test", "Test", "Test", LocalDateTime.now(), LocalDateTime.now());

    when(voucherRepository.findById(any())).thenReturn(Optional.of(voucher));
    Voucher deletedVoucher = voucherService.deleteById(id);

    assertEquals(deletedVoucher, voucher);
    verify(voucherRepository).deleteById(any());
  }

  @Test
  public void testGivenIdToBeDeleted_thenThrowVoucherNotFound() throws Exception {
    Long id = 1L;

    when(voucherRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(VoucherNotFoundException.class, () -> {
      voucherService.deleteById(id);
    });
  }

  @Test
  public void testGivenIdAndVoucherRequest_thenUpdateAndReturnUpdatedVoucher() throws Exception {
    Long id = 1L;
    VoucherRequest request = new VoucherRequest("Test2", "Test", "Test", LocalDateTime.now(), LocalDateTime.now());
    Voucher voucher = new Voucher("Test", "Test", "Test", LocalDateTime.now(), LocalDateTime.now());
    Voucher expectedVoucher = new Voucher("Test2", "Test", "Test", LocalDateTime.now(), LocalDateTime.now());

    when(voucherRepository.findById(any())).thenReturn(Optional.of(voucher));
    when(voucherRepository.save(any())).thenReturn(expectedVoucher);
    Voucher updatedVoucher = voucherService.update(id, request);

    assertEquals(updatedVoucher, expectedVoucher);
    verify(voucherRepository).save(voucherArgumentCaptor.capture());
    assertEquals(voucherArgumentCaptor.getValue().getName(), expectedVoucher.getName());
  }

  @Test
  public void testGivenIdAndVoucherRequest_thenThrowVoucherNotFound() throws Exception {
    Long id = 1L;
    VoucherRequest request = new VoucherRequest("Test2", "Test", "Test", LocalDateTime.now(), LocalDateTime.now());

    when(voucherRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(VoucherNotFoundException.class, () -> {
      voucherService.update(id, request);
    });
  }
}
