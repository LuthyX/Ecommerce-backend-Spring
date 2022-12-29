package com.example.Ecommerce.service;

import com.example.Ecommerce.api.model.LoginBody;
import com.example.Ecommerce.api.model.RegistrationBody;
import com.example.Ecommerce.exception.EmailFailureException;
import com.example.Ecommerce.exception.UserAlreadyExistsException;
import com.example.Ecommerce.model.AppUser;
import com.example.Ecommerce.model.Role;
import com.example.Ecommerce.model.VerificationToken;
import com.example.Ecommerce.repository.AppUserRepository;
import com.example.Ecommerce.repository.VerificationTokenRepostiory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AppUserService {


    private AppUserRepository appUserRepository;
    private EncryptionService encryptionService;
    private JWTService jwtService;
    private EmailService emailService;
    private VerificationTokenRepostiory verificationTokenRepostiory;

    public AppUserService(AppUserRepository appUserRepository, EncryptionService encryptionService, JWTService jwtService, EmailService emailService, VerificationTokenRepostiory verificationTokenRepostiory){
        this.appUserRepository = appUserRepository;
        this.encryptionService = encryptionService;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.verificationTokenRepostiory = verificationTokenRepostiory;
    }

    public AppUser registerUser (RegistrationBody registrationBody) throws UserAlreadyExistsException, EmailFailureException {

        if (appUserRepository.findByUsername(registrationBody.getUsername()).isPresent()
                || appUserRepository.findByEmail(registrationBody.getEmail()).isPresent()){
            throw new UserAlreadyExistsException();
        }

        AppUser appUser = new AppUser();
        appUser.setUsername(registrationBody.getUsername());
        appUser.setLastName(registrationBody.getLastName());
        appUser.setFirstName(registrationBody.getFirstName());
        appUser.setEmail(registrationBody.getEmail());
        appUser.setCreateTime(LocalDateTime.now());
        appUser.setRole(Role.USER);
        appUser.setPassword(encryptionService.encryptPassword(registrationBody.getPassword()));
        VerificationToken verificationToken = createVerificationToken(appUser);
        emailService.sendVerificationMail(verificationToken);
        verificationTokenRepostiory.save(verificationToken);
        return appUserRepository.save(appUser);

    }

    public String loginUser(@RequestBody LoginBody loginBody){
        Optional<AppUser> optionalAppUser = appUserRepository.findByUsername(loginBody.getUsername());
        if (optionalAppUser.isPresent()){
                AppUser user = optionalAppUser.get();
                if (encryptionService.verifyPassword(loginBody.getPassword(), user.getPassword())){
                   return jwtService.generateJWT(user);
                }
        }
        return null;
    }

    private VerificationToken createVerificationToken(AppUser appUser){
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(jwtService.generateVerificationJWT(appUser));
        verificationToken.setCreatedTimestamp(new Timestamp(System.currentTimeMillis()));
        verificationToken.setAppUser(appUser);
        appUser.getVerificationTokens().add(verificationToken);
        return verificationToken;
    }

}
