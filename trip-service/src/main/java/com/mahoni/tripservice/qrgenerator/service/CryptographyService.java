package com.mahoni.tripservice.qrgenerator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

@Service
@Slf4j
public class CryptographyService {
//  private static SecretKeySpec secretKey;
//  private static byte[] key;
  private static final String ALGORITHM = "AES";

  public SecretKeySpec prepareSecreteKey(String myKey) throws NoSuchAlgorithmException {
    byte[] key = myKey.getBytes(StandardCharsets.UTF_8);
    MessageDigest sha = MessageDigest.getInstance("SHA-1");
    key = sha.digest(key);
    key = Arrays.copyOf(key, 16);
    return new SecretKeySpec(key, ALGORITHM);
  }

  public String encrypt(String strToEncrypt, String secret) {
    try {
      log.info("Trying to encrypt: " + strToEncrypt + " " + secret);
      SecretKeySpec secretKey = prepareSecreteKey(secret);
      Cipher cipher = Cipher.getInstance(ALGORITHM);
      cipher.init(Cipher.ENCRYPT_MODE, secretKey);
      return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
    } catch (Exception e) {
      log.error("Error while encrypting: " + e);
    }
    return null;
  }

  public String decrypt(String strToDecrypt, String secret) {
    try {
      log.info("Trying to decrypt: " + strToDecrypt + " " + secret);
      SecretKeySpec secretKey = prepareSecreteKey(secret);
      Cipher cipher = Cipher.getInstance(ALGORITHM);
      cipher.init(Cipher.DECRYPT_MODE, secretKey);
      return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
    } catch (Exception e) {
      log.error("Error while decrypting: " + e);
    }
    return null;
  }
}
