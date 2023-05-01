package com.mahoni.voucherservice.merchant.repository;

import com.mahoni.voucherservice.merchant.model.Merchant;
import com.mahoni.voucherservice.merchant.model.MerchantRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
