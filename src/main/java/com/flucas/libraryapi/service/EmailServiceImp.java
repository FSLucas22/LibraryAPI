package com.flucas.libraryapi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.flucas.libraryapi.service.interfaces.EmailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImp implements EmailService{

    @Value("{application.mail.default-remetent}")
    private String remetent;

    private final JavaMailSender javaMailSender;
    
    @Override
    public void sendMails(List<String> emails, String message) {
        var mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(remetent);
        mailMessage.setSubject("Livro com empréstimo atrasado");
        mailMessage.setText(message);
        mailMessage.setTo(emails.toArray(new String[emails.size()]));
        javaMailSender.send(mailMessage);
    }
    
}
