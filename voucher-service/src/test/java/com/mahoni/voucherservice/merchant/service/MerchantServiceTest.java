package com.mahoni.voucherservice.merchant.service;

import com.mahoni.voucherservice.merchant.dto.MerchantRequest;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MerchantServiceTest {

  @Mock
  MerchantRepository merchantRepository;

  @InjectMocks
  MerchantService merchantService;

  @Captor
  ArgumentCaptor<Merchant> merchantArgumentCaptor;

  @Test
  public void testGivenMerchantRequest_thenSaveMerchant() throws Exception {
    MerchantRequest request = new MerchantRequest("Test", "Test", "Test@mail.com");
    Merchant merchant = new Merchant("Test", "Test", "Test@mail.com");

    when(merchantRepository.findByUsername(any())).thenReturn(Optional.empty());
    when(merchantRepository.save(any())).thenReturn(merchant);
    Merchant savedMerchant = merchantService.create(request);

    assertEquals(savedMerchant, merchant);
    verify(merchantRepository).save(any());
  }

  @Test
  public void testGivenMerchantRequest_thenThrowMerchantAlreadyExist() throws Exception {
    MerchantRequest request = new MerchantRequest("Test", "Test", "Test@mail.com");
    Merchant merchant = new Merchant("Test", "Test", "Test@mail.com");

    when(merchantRepository.findByUsername(any())).thenReturn(Optional.of(merchant));

    assertThrows(MerchantAlreadyExistException.class, () -> {
      merchantService.create(request);
    });
  }

  @Test
  public void testGivenId_thenReturnMerchant() throws Exception {
    UUID id = UUID.randomUUID();
    Merchant merchant = new Merchant("Test", "Test", "Test@mail.com");

    when(merchantRepository.findById(any())).thenReturn(Optional.of(merchant));
    Merchant savedMerchant = merchantService.getById(id);

    assertEquals(savedMerchant, merchant);
    verify(merchantRepository).findById(any());
  }

  @Test
  public void testGivenId_thenThrowMerchantNotFound() throws Exception {
    UUID id = UUID.randomUUID();

    when(merchantRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(MerchantNotFoundException.class, () -> {
      merchantService.getById(id);
    });
  }

  @Test
  public void testGetAll_thenReturnMerchants() throws Exception {
    Merchant merchant = new Merchant("Test", "Test", "Test@mail.com");
    List<Merchant> merchants = new ArrayList<>();
    merchants.add(merchant);

    when(merchantRepository.findAll()).thenReturn(merchants);
    List<Merchant> savedMerchants = merchantService.getAll();

    assertEquals(savedMerchants, merchants);
    verify(merchantRepository).findAll();
  }

  @Test
  public void testGivenIdToBeDeleted_thenDeleteAndReturnDeletedMerchant() throws Exception {
    UUID id = UUID.randomUUID();
    Merchant merchant = new Merchant("Test", "Test", "Test@mail.com");

    when(merchantRepository.findById(any())).thenReturn(Optional.of(merchant));
    Merchant deletedMerchant = merchantService.deleteById(id);

    assertEquals(deletedMerchant, merchant);
    verify(merchantRepository).deleteById(any());
  }

  @Test
  public void testGivenIdToBeDeleted_thenThrowMerchantNotFound() throws Exception {
    UUID id = UUID.randomUUID();

    when(merchantRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(MerchantNotFoundException.class, () -> {
      merchantService.deleteById(id);
    });
  }

  @Test
  public void testGivenIdAndMerchantRequest_thenUpdateAndReturnUpdatedMerchant() throws Exception {
    UUID id = UUID.randomUUID();
    MerchantRequest request = new MerchantRequest("Test2", "Test", "Test@mail.com" );
    Merchant merchant = new Merchant("Test", "Test", "Test@mail.com");
    Merchant expectedMerchant = new Merchant("Test2", "Test", "Test@mail.com");

    when(merchantRepository.findById(any())).thenReturn(Optional.of(merchant));
    when(merchantRepository.save(any())).thenReturn(expectedMerchant);
    Merchant updatedMerchant = merchantService.update(id, request);

    assertEquals(updatedMerchant, expectedMerchant);
    verify(merchantRepository).save(merchantArgumentCaptor.capture());
    assertEquals(merchantArgumentCaptor.getValue().getUsername(), expectedMerchant.getUsername());
  }

  @Test
  public void testGivenIdAndMerchantRequest_thenThrowMerchantNotFound() throws Exception {
    UUID id = UUID.randomUUID();
    MerchantRequest request = new MerchantRequest("Test2", "Test", "Test@mail.com" );

    when(merchantRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(MerchantNotFoundException.class, () -> {
      merchantService.update(id, request);
    });
  }
}
