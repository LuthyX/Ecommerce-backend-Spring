package com.example.Ecommerce.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.Ecommerce.model.AppUser;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JWTService {

    @Value("${jwt.algorithm.key}")
    private String algorithmKey;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.expiryInSeconds}")
    private int expiryInSeconds;

    private Algorithm algorithm;

    private final static String USERNAME_KEY = "USERNAME";
    private final static String EMAIL_KEY = "EMAIL";

    @PostConstruct
    public void postConstruct(){
        algorithm = Algorithm.HMAC256(algorithmKey);
    }

    public String generateJWT(AppUser appUser){
        return JWT.create().withClaim(USERNAME_KEY, appUser.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + ( 1000* expiryInSeconds)))
                .withIssuer(issuer)
                .sign(algorithm);
    }

    public String generateVerificationJWT(AppUser appUser){
        return JWT.create().withClaim(EMAIL_KEY, appUser.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + ( 1000* expiryInSeconds)))
                .withIssuer(issuer)
                .sign(algorithm);
    }

    public String getUsername(String token){
        DecodedJWT jwt = JWT.require(algorithm).withIssuer(issuer).build().verify(token);
        return jwt.getClaim(USERNAME_KEY).asString();
    }

}
