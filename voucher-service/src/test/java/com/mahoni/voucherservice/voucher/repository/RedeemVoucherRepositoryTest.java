package com.mahoni.voucherservice.voucher.repository;

import com.mahoni.voucherservice.merchant.model.Merchant;
import com.mahoni.voucherservice.merchant.model.MerchantRole;
import com.mahoni.voucherservice.voucher.model.RedeemVoucher;
import com.mahoni.voucherservice.voucher.model.Voucher;
import com.mahoni.voucherservice.voucher.model.VoucherStatus;
import com.mahoni.voucherservice.voucher.model.VoucherType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class RedeemVoucherRepositoryTest {

  @Autowired
  private RedeemVoucherRepository redeemVoucherRepository;

  @Autowired
  private TestEntityManager testEntityManager;

  @Test
  public void testFIndAllByUserId() {
    UUID id = UUID.randomUUID();
    Merchant merchant = new Merchant("Test", "Test", "Test@mail.com", "Test", MerchantRole.MERCHANT);
    Voucher voucher = new Voucher("Test", "Test", VoucherType.FOOD_AND_BEVERAGES, 1, LocalDateTime.now(), LocalDateTime.now(), merchant);
    RedeemVoucher redeemVoucher1 = new RedeemVoucher(voucher, "Test1", LocalDateTime.now());
    RedeemVoucher redeemVoucher2 = new RedeemVoucher(voucher, "Test2", LocalDateTime.now());
    RedeemVoucher redeemVoucher3 = new RedeemVoucher(voucher, "Test3", LocalDateTime.now());
    redeemVoucher1.setUserId(id);
    redeemVoucher2.setUserId(id);
    redeemVoucher3.setUserId(UUID.randomUUID());

    testEntityManager.persist(merchant);
    testEntityManager.persist(voucher);
    testEntityManager.persist(redeemVoucher1);
    testEntityManager.persist(redeemVoucher2);
    testEntityManager.persist(redeemVoucher3);

    List<RedeemVoucher> redeemVouchers = redeemVoucherRepository.findAllByUserId(id);

    assertFalse(redeemVouchers.isEmpty());
    assertEquals(2, redeemVouchers.size());
    assertEquals(id, redeemVouchers.get(0).getUserId());
    assertEquals(id, redeemVouchers.get(1).getUserId());
  }

  @Test
  public void testFindAllByStatus() {
    Merchant merchant = new Merchant("Test", "Test", "Test@mail.com", "Test", MerchantRole.MERCHANT);
    Voucher voucher = new Voucher("Test", "Test", VoucherType.FOOD_AND_BEVERAGES, 1, LocalDateTime.now(), LocalDateTime.now(), merchant);
    RedeemVoucher redeemVoucher1 = new RedeemVoucher(voucher, "Test1", LocalDateTime.now());
    RedeemVoucher redeemVoucher2 = new RedeemVoucher(voucher, "Test2", LocalDateTime.now());
    RedeemVoucher redeemVoucher3 = new RedeemVoucher(voucher, "Test3", LocalDateTime.now());
    redeemVoucher1.setStatus(VoucherStatus.ACTIVE);
    redeemVoucher2.setStatus(VoucherStatus.ACTIVE);
    redeemVoucher3.setStatus(VoucherStatus.REDEEMED);

    testEntityManager.persist(merchant);
    testEntityManager.persist(voucher);
    testEntityManager.persist(redeemVoucher1);
    testEntityManager.persist(redeemVoucher2);
    testEntityManager.persist(redeemVoucher3);

    List<RedeemVoucher> redeemVouchers = redeemVoucherRepository.findAllByStatus(VoucherStatus.ACTIVE);

    assertFalse(redeemVouchers.isEmpty());
    assertEquals(2, redeemVouchers.size());
    assertEquals(VoucherStatus.ACTIVE, redeemVouchers.get(0).getStatus());
    assertEquals(VoucherStatus.ACTIVE, redeemVouchers.get(1).getStatus());
  }

  @Test
  public void testFindAvailableRedeemVoucherByVoucherId() {
    Merchant merchant = new Merchant("Test", "Test", "Test@mail.com", "Test", MerchantRole.MERCHANT);
    Voucher voucher1 = new Voucher("Test", "Test", VoucherType.FOOD_AND_BEVERAGES, 1, LocalDateTime.now(), LocalDateTime.now(), merchant);
    Voucher voucher2 = new Voucher("Test", "Test", VoucherType.FOOD_AND_BEVERAGES, 1, LocalDateTime.now(), LocalDateTime.now(), merchant);

    testEntityManager.persist(merchant);
    testEntityManager.persist(voucher1);
    testEntityManager.persist(voucher2);
    testEntityManager.persist(new RedeemVoucher(voucher1, "Test1", LocalDateTime.now()));
    testEntityManager.persist(new RedeemVoucher(voucher1, "Test2", LocalDateTime.now()));
    testEntityManager.persist(new RedeemVoucher(voucher2, "Test3", LocalDateTime.now()));

    Optional<RedeemVoucher> redeemVoucher = redeemVoucherRepository.findAvailableRedeemVoucherByVoucherId(voucher1.getId());

    assertTrue(redeemVoucher.isPresent());
    assertEquals(voucher1.getId(), redeemVoucher.get().getVoucher().getId());
    assertEquals("Test1", redeemVoucher.get().getRedeemCode());
  }

  @Test
  public void testFindAllByMerchant() {
    Merchant merchant1 = new Merchant("Test1", "Test", "Test@mail.com", "Test", MerchantRole.MERCHANT);
    Merchant merchant2 = new Merchant("Test2", "Test", "Test@mail.com", "Test", MerchantRole.MERCHANT);
    Voucher voucher1 = new Voucher("Test1", "Test", VoucherType.FOOD_AND_BEVERAGES, 1, LocalDateTime.now(), LocalDateTime.now(), merchant1);
    Voucher voucher2 = new Voucher("Test2", "Test", VoucherType.FOOD_AND_BEVERAGES, 1, LocalDateTime.now(), LocalDateTime.now(), merchant2);

    testEntityManager.persist(merchant1);
    testEntityManager.persist(merchant2);
    testEntityManager.persist(voucher1);
    testEntityManager.persist(voucher2);
    testEntityManager.persist(new RedeemVoucher(voucher1, "Test1", LocalDateTime.now()));
    testEntityManager.persist(new RedeemVoucher(voucher1, "Test2", LocalDateTime.now()));
    testEntityManager.persist(new RedeemVoucher(voucher2, "Test3", LocalDateTime.now()));

    List<RedeemVoucher> redeemVouchers = redeemVoucherRepository.findAllByMerchant("Test1");

    assertFalse(redeemVouchers.isEmpty());
    assertEquals(2, redeemVouchers.size());
    assertEquals("Test1", redeemVouchers.get(0).getVoucher().getMerchant().getUsername());
    assertEquals("Test1", redeemVouchers.get(1).getVoucher().getMerchant().getUsername());
  }
}
