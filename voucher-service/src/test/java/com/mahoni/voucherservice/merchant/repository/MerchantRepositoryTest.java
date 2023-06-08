package com.mahoni.voucherservice.merchant.repository;

import com.mahoni.voucherservice.merchant.model.Merchant;
import com.mahoni.voucherservice.merchant.model.MerchantRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class MerchantRepositoryTest {

  @Autowired
  private MerchantRepository merchantRepository;

  @Autowired
  private TestEntityManager testEntityManager;

  @Test
  public void testFindByUsername() {
    testEntityManager.persist(new Merchant("Test", "Test", "Test@mail.com", "Test", MerchantRole.MERCHANT));

    Optional<Merchant> merchant = merchantRepository.findByUsername("Test");

    assertTrue(merchant.isPresent());
    assertEquals("Test", merchant.get().getUsername());
  }

  @Test
  public void testFindByRole() {
    testEntityManager.persist(new Merchant("Test", "Test", "Test@mail.com", "Test", MerchantRole.MERCHANT));
    testEntityManager.persist(new Merchant("Test2", "Test2", "Test2@mail.com", "Test", MerchantRole.ADMIN));

    List<Merchant> merchants = merchantRepository.findAllByRole(MerchantRole.MERCHANT);

    assertFalse(merchants.isEmpty());
    assertEquals(1, merchants.size());
    assertEquals(MerchantRole.MERCHANT, merchants.get(0).getRole());
  }
}
