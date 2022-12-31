package com.example.Ecommerce.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EncryptionServiceTest {


    @Autowired
    private EncryptionService encryptionService;

    @Test
    public void PasswordEncryptionTest(){
        String password = "PasswordA123";
        Assertions.assertNotNull(encryptionService.encryptPassword(password), "THats the encrypted password");
        String hash = encryptionService.encryptPassword(password);
        Assertions.assertTrue(encryptionService.verifyPassword(password, hash),"Password and Hash-Password should return true");
        Assertions.assertFalse(encryptionService.verifyPassword(password + "fake", hash), "Password and Hash-Password should return false");
    }

}
