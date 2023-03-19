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
      voucher.getStart_at(),
      voucher.getExpired_at()));
  }

  public Voucher getById(Long id) {
    Optional<Voucher> voucher = voucherRepository.findById(id);
    if (voucher.isEmpty()) {
      throw new VoucherNotFoundException(id);
    }
    return voucher.get();
  }

  public List<Voucher> getAll() {
    return voucherRepository.findAll();
  }

  public Voucher deleteById(Long id) {
    Optional<Voucher> voucher = voucherRepository.findById(id);
    if (voucher.isEmpty()) {
      throw new VoucherNotFoundException(id);
    }
    voucherRepository.deleteById(id);
    return voucher.get();
  }

  public Voucher update(Long id, VoucherRequest newVoucher) {
    Optional<Voucher> voucher = voucherRepository.findById(id);
    if (voucher.isEmpty()) {
      throw new VoucherNotFoundException();
    }
    Voucher updatedVoucher = voucher.get();
    updatedVoucher.setName(newVoucher.getName());
    updatedVoucher.setDescription(newVoucher.getDescription());
    updatedVoucher.setCode(newVoucher.getCode());
    updatedVoucher.setStart_at(newVoucher.getStart_at());
    updatedVoucher.setExpired_at(newVoucher.getExpired_at());
    return voucherRepository.save(updatedVoucher);
  }
}