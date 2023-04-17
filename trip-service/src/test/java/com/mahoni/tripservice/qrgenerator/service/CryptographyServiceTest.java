package com.mahoni.tripservice.qrgenerator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class CryptographyServiceTest {
  @InjectMocks
  CryptographyService cryptographyService;

  @Test
  public void testGivenStringToEncryptAndSecret_thenSuccess() {
    String strToEncrypt = "Hello, World!";
    String secret = "mySecretKey";

    String encrypted = cryptographyService.encrypt(strToEncrypt, secret);
    String decrypted = cryptographyService.decrypt(encrypted, secret);

    assertEquals(strToEncrypt, decrypted);
  }

  @Test
  public void testGivenStringToDecryptAndSecret_thenSuccess() {
    String strToDecrypt = "ybo92e29LLOlb5fXBkhg2g==";
    String secret = "mySecretKey";

    String decrypted = cryptographyService.decrypt(strToDecrypt, secret);
    String encrypted = cryptographyService.encrypt(decrypted, secret);

    assertEquals(strToDecrypt, encrypted);
  }
}
