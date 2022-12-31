package com.example.Ecommerce.service;

import com.example.Ecommerce.model.AppUser;
import com.example.Ecommerce.repository.AppUserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JWTServiceTest {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private AppUserRepository appUserRepository;

    @Test
    public void testVerificationTokenNotUsableForLogin(){
        AppUser appUser = appUserRepository.findByUsername("user1name").get();
        String token = jwtService.generateVerificationJWT(appUser);
        Assertions.assertNull(jwtService.getUsername(token), "Verification token should not contain Username");
    }

    @Test
    public void testAuthTokenReturnsUsername(){
        AppUser appUser = appUserRepository.findByUsername("user1name").get();
        String token = jwtService.generateJWT(appUser);
        Assertions.assertEquals(appUser.getUsername(), jwtService.getUsername(token), "AuthToken should should produce Username  = to AppUser's Username");
    }
}
