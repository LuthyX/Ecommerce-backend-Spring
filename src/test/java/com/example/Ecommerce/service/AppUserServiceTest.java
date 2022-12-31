package com.example.Ecommerce.service;

import com.example.Ecommerce.api.model.LoginBody;
import com.example.Ecommerce.api.model.RegistrationBody;
import com.example.Ecommerce.exception.EmailFailureException;
import com.example.Ecommerce.exception.UserAlreadyExistsException;
import com.example.Ecommerce.exception.UserNotVerifiedException;
import com.example.Ecommerce.model.VerificationToken;
import com.example.Ecommerce.repository.VerificationTokenRepostiory;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
public class AppUserServiceTest {
    
    @RegisterExtension
    private static GreenMailExtension greenMailExtension = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("springboot", "secret"))
            .withPerMethodLifecycle(true);

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private VerificationTokenRepostiory verificationTokenRepostiory;

    @Test
    @Transactional
    public void testRegisterUser() throws MessagingException {
        RegistrationBody body = new RegistrationBody();
        body.setEmail("User@Junit.com");
        body.setFirstName("User1F");
        body.setLastName("User1L");
        body.setPassword("UserPassword");
        body.setUsername("user1name");
        Assertions.assertThrows(UserAlreadyExistsException.class, () -> appUserService.registerUser(body), "Username already in use");
        body.setUsername("UserNameJunit");
        body.setEmail("user1@user.com");
        Assertions.assertThrows(UserAlreadyExistsException.class, ()-> appUserService.registerUser(body), "Email already in use");
        body.setEmail("User@Junit.com");
        Assertions.assertDoesNotThrow(()-> appUserService.registerUser(body), "User can Successfully Register" );
        Assertions.assertEquals(body.getEmail(), greenMailExtension.getReceivedMessages()[0].getRecipients(Message.RecipientType.TO)[0].toString());
    }

    @Test
    @Transactional
    public void loginUserTest() throws UserNotVerifiedException, EmailFailureException {
        LoginBody body = new LoginBody();
        body.setUsername("UsernameDNE");
        body.setPassword("User1234");
        Assertions.assertNull(appUserService.loginUser(body), "The User should not exist");
        body.setUsername("user1name");
        body.setPassword("PasswordDNE");
        Assertions.assertNull(appUserService.loginUser(body), "Password is not correct");
        body.setPassword("PasswordA123");
        Assertions.assertNotNull(appUserService.loginUser(body), "User should Login now");
        body.setUsername("user2name");
        body.setPassword("PasswordA123");
        try{
            appUserService.loginUser(body);
            Assertions.assertTrue(false, "Email not verifed by User");
        }
        catch (UserNotVerifiedException ex) {
            Assertions.assertTrue(ex.isEmailSent(), "Email is now sent");
            Assertions.assertEquals(1, greenMailExtension.getReceivedMessages().length);
        }
        try{
            appUserService.loginUser(body);
            Assertions.assertTrue(false, "Email not verifed by User");
        }
        catch (UserNotVerifiedException ex) {
            Assertions.assertFalse(ex.isEmailSent(), "Email should not be sent");
            Assertions.assertEquals(1, greenMailExtension.getReceivedMessages().length);
        }

        }

    @Test
    @Transactional
    public void verifyUserTest() throws EmailFailureException {
        Assertions.assertFalse(appUserService.verifyUser("Bad token"), "Not a valid token");
//        Assertions.assertNull(appUserService.verifyUser("Bad Token"), "Not a valid token");
        LoginBody body = new LoginBody();
        body.setUsername("user2name");
        body.setPassword("PasswordA123");
        try{
            appUserService.loginUser(body);
            Assertions.assertTrue(false,"email should not be verified");
        }
        catch (
                UserNotVerifiedException ex
        ){
            List<VerificationToken> tokens = verificationTokenRepostiory.findByUser_IdOrderByIdDesc(2L);
            String token = tokens.get(0).getToken();
            Assertions.assertTrue(appUserService.verifyUser(token), "Token is now validated");
            Assertions.assertNotNull(body, "User should now be valid");
        }
    }
    }


