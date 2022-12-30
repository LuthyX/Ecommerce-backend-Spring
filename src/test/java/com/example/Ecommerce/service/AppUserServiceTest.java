package com.example.Ecommerce.service;

import com.example.Ecommerce.api.model.RegistrationBody;
import com.example.Ecommerce.exception.UserAlreadyExistsException;
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

@SpringBootTest
public class AppUserServiceTest {


    @RegisterExtension
    private static GreenMailExtension greenMailExtension = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("springboot", "secret"))
            .withPerMethodLifecycle(true);

    @Autowired
    private AppUserService appUserService;

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

}
