package com.example.Ecommerce.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.MissingClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.example.Ecommerce.model.AppUser;
import com.example.Ecommerce.repository.AppUserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JWTServiceTest {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private AppUserRepository appUserRepository;

    @Value("${jwt.algorithm.key}")
    private String algorithmKey;

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

    @Test
    public void testJWTNotGeneratedByUs(){
        String token = JWT.create().withClaim("USERNAME", "USER1NAME").sign(Algorithm.HMAC256("JustATestAlgoKey"));
        Assertions.assertThrows(SignatureVerificationException.class, ()-> jwtService.getUsername(token));
    }

    @Test
    public void testJWTCorrectlySignedNoIssuer(){
        String token = JWT.create().withClaim("USERNAME", "USER1NAME").sign(Algorithm.HMAC256(algorithmKey));
        Assertions.assertThrows(MissingClaimException.class, ()-> jwtService.getUsername(token));
    }
}
