package com.mahoni.voucherservice.voucher.repository;

import com.mahoni.voucherservice.merchant.model.Merchant;
import com.mahoni.voucherservice.merchant.model.MerchantRole;
import com.mahoni.voucherservice.voucher.model.Voucher;
import com.mahoni.voucherservice.voucher.model.VoucherType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class VoucherRepositoryTest {

  @Autowired
  private VoucherRepository voucherRepository;

  @Autowired
  private TestEntityManager testEntityManager;

  @Test
  public void testFindAll() {
    Merchant merchant = new Merchant("Test", "Test", "Test@mail.com", "Test", MerchantRole.MERCHANT);

    testEntityManager.persist(merchant);
    testEntityManager.persist(new Voucher("Test", "Test", VoucherType.FNB, 1, LocalDateTime.now(), LocalDateTime.now(), merchant));
    testEntityManager.persist(new Voucher("Test", "Test", VoucherType.FNB, 1, LocalDateTime.now(), LocalDateTime.now(), merchant));

    List<Voucher> vouchers = voucherRepository.findAll();

    assertFalse(vouchers.isEmpty());
    assertEquals(2, vouchers.size());
  }

}
