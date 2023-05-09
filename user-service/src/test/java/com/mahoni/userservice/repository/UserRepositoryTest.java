package com.mahoni.userservice.repository;

import com.mahoni.userservice.model.Sex;
import com.mahoni.userservice.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private TestEntityManager testEntityManager;

  @Test
  public void testFIndByUsername() {
    testEntityManager.persist(new User("test1", "Test1", "test1@mail.com", Sex.NOT_APPLICABLE, 2000, 0));
    testEntityManager.persist(new User("test2", "Test2", "test2@mail.com", Sex.MALE, 2000, 0));
    testEntityManager.persist(new User("test3", "Test3", "test3@mail.com", Sex.FEMALE, 2000, 0));

    Optional<User> user = userRepository.findByUsername("test1");

    assertTrue(user.isPresent());
    assertEquals("test1", user.get().getUsername());
  }
}
