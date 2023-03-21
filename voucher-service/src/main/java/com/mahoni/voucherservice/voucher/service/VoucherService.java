package com.mahoni.voucherservice.voucher.service;

import com.mahoni.voucherservice.voucher.dto.VoucherRequest;
import com.mahoni.voucherservice.voucher.exception.VoucherAlreadyExistException;
import com.mahoni.voucherservice.voucher.exception.VoucherNotFoundException;
import com.mahoni.voucherservice.voucher.model.Voucher;
import com.mahoni.voucherservice.voucher.repository.VoucherRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class VoucherService {
  @Autowired
  VoucherRepository voucherRepository;

  public Voucher create(VoucherRequest voucher) {
//    TODO: request user, continue if merchant
    if (voucherRepository.findByName(voucher.getName()).isPresent() &&
      voucherRepository.findByCode(voucher.getCode()).isPresent()) {
      throw new VoucherAlreadyExistException(voucher.getName(), voucher.getCode());
    }
    return voucherRepository.save(new Voucher(voucher.getName(),
      voucher.getDescription(),
      voucher.getCode(),
      voucher.getStartAt(),
      voucher.getExpiredAt()));
  }

  public Voucher getById(UUID id) {
    Optional<Voucher> voucher = voucherRepository.findById(id);
    if (voucher.isEmpty()) {
      throw new VoucherNotFoundException(id);
    }
    return voucher.get();
  }

  public List<Voucher> getAll() {
    return voucherRepository.findAll();
  }

  public Voucher deleteById(UUID id) {
    Optional<Voucher> voucher = voucherRepository.findById(id);
    if (voucher.isEmpty()) {
      throw new VoucherNotFoundException(id);
    }
    voucherRepository.deleteById(id);
    return voucher.get();
  }

  public Voucher update(UUID id, VoucherRequest newVoucher) {
    Optional<Voucher> voucher = voucherRepository.findById(id);
    if (voucher.isEmpty()) {
      throw new VoucherNotFoundException();
    }
    Voucher updatedVoucher = voucher.get();
    updatedVoucher.setName(newVoucher.getName());
    updatedVoucher.setDescription(newVoucher.getDescription());
    updatedVoucher.setCode(newVoucher.getCode());
    updatedVoucher.setStartAt(newVoucher.getStartAt());
    updatedVoucher.setExpiredAt(newVoucher.getExpiredAt());
    return voucherRepository.save(updatedVoucher);
  }
}
