package com.example.Ecommerce.service;

import com.example.Ecommerce.exception.EmailFailureException;
import com.example.Ecommerce.model.VerificationToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${email.from}")
    private String fromAddress;

    @Value("${app.frontend.url}")
    private String url;


    private JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }
    private SimpleMailMessage makeMailMessage(){
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(fromAddress);
        return simpleMailMessage;
    }
    public void sendVerificationMail(VerificationToken verificationToken) throws EmailFailureException {
        SimpleMailMessage message = makeMailMessage();
        message.setTo(verificationToken.getUser().getEmail());
        message.setSubject("Verify Your Email To Activate Your Account");
        message.setText("Please the follow the link below to verify your email account. \n"
                + url + "/auth/verify?token=" + verificationToken.getToken());
        try{
            javaMailSender.send(message);
        }
        catch (MailException ex){
            throw new EmailFailureException();
        }
    }
}
