package com.example.Ecommerce.service;

import com.example.Ecommerce.api.model.LoginBody;
import com.example.Ecommerce.api.model.RegistrationBody;
import com.example.Ecommerce.exception.EmailDoesNotExistException;
import com.example.Ecommerce.exception.EmailFailureException;
import com.example.Ecommerce.exception.UserAlreadyExistsException;
import com.example.Ecommerce.exception.UserNotVerifiedException;
import com.example.Ecommerce.model.AppUser;
import com.example.Ecommerce.model.PasswordResetToken;
import com.example.Ecommerce.model.Role;
import com.example.Ecommerce.model.VerificationToken;
import com.example.Ecommerce.repository.AppUserRepository;
import com.example.Ecommerce.repository.PasswordResetTokenRepository;
import com.example.Ecommerce.repository.VerificationTokenRepostiory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppUserService {


    private AppUserRepository appUserRepository;
    private EncryptionService encryptionService;
    private JWTService jwtService;
    private EmailService emailService;
    private VerificationTokenRepostiory verificationTokenRepostiory;
    private PasswordResetTokenRepository passwordResetTokenRepository;

    public AppUserService(AppUserRepository appUserRepository, EncryptionService encryptionService, JWTService jwtService, EmailService emailService, VerificationTokenRepostiory verificationTokenRepostiory, PasswordResetTokenRepository passwordResetTokenRepository){
        this.appUserRepository = appUserRepository;
        this.encryptionService = encryptionService;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.verificationTokenRepostiory = verificationTokenRepostiory;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
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
        return appUserRepository.save(appUser);
    }

    public String loginUser(@RequestBody LoginBody loginBody) throws EmailFailureException, UserNotVerifiedException {
        Optional<AppUser> optionalAppUser = appUserRepository.findByUsername(loginBody.getUsername());
        if (optionalAppUser.isPresent()){
                AppUser user = optionalAppUser.get();
                if (encryptionService.verifyPassword(loginBody.getPassword(), user.getPassword())){
                    if(user.getEmailVerified()) {
                        return jwtService.generateJWT(user);
                    }
                    else {
                        List<VerificationToken> verificationToken = user.getVerificationTokens();
                        Boolean resend = verificationToken.size() == 0 || verificationToken.get(0).getCreatedTimestamp().before(new Timestamp(System.currentTimeMillis() - (60*60*60*1000)));
                        if(resend){
                            VerificationToken verificationToken1 = createVerificationToken(user);
                            emailService.sendVerificationMail(verificationToken1);
                            verificationTokenRepostiory.save(verificationToken1);
                        }
                        throw new UserNotVerifiedException(resend);
                    }
                }
        }
        return null;
    }

    private VerificationToken createVerificationToken(AppUser appUser){
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(jwtService.generateVerificationJWT(appUser));
        verificationToken.setCreatedTimestamp(new Timestamp(System.currentTimeMillis()));
        verificationToken.setUser(appUser);
        appUser.getVerificationTokens().add(verificationToken);
        return verificationToken;
    }

    @Transactional
    public Boolean verifyUser (String token){
        Optional<VerificationToken> optoken = verificationTokenRepostiory.findByToken(token);
        if(optoken.isPresent()){
            VerificationToken verificationToken = optoken.get();
            AppUser appUser = verificationToken.getUser();
            if(!appUser.getEmailVerified()){
                appUser.setEmailVerified(true);
                appUserRepository.save(appUser);
                verificationTokenRepostiory.deleteByUser(appUser);
                return true;
            }
        }
        return false;
    }

    public void forgotEmail(String email) throws EmailDoesNotExistException, EmailFailureException {
        Optional<AppUser> optapp = appUserRepository.findByEmail(email);
        if (!optapp.isPresent()){
            throw new EmailDoesNotExistException();
        }
        AppUser appUser = optapp.get();
        String token = jwtService.generatePasswordResetJWT(appUser);
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setCreated_at(LocalDateTime.now());
        passwordResetToken.setToken(token);
        passwordResetToken.setUser(appUser);
        emailService.sendPasswordResetEmail(passwordResetToken);
        passwordResetTokenRepository.save(passwordResetToken);
//        appUser.getPasswordResetTokens().add(passwordResetToken);
    }

    public void

}
