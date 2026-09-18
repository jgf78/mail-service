package com.julian.mail_service.service.impl;


import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.julian.mail_service.dto.EmailRequest;
import com.julian.mail_service.service.EmailSenderService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailSenderServiceImpl implements EmailSenderService {

    private final JavaMailSender mailSender;

    public EmailSenderServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void send(EmailRequest request) {

        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(request.to().toArray(new String[0]));

            if (request.cc() != null && !request.cc().isEmpty()) {
                helper.setCc(request.cc().toArray(new String[0]));
            }

            if (request.bcc() != null && !request.bcc().isEmpty()) {
                helper.setBcc(request.bcc().toArray(new String[0]));
            }

            if (request.replyTo() != null && !request.replyTo().isBlank()) {
                helper.setReplyTo(request.replyTo());
            }

            helper.setSubject(request.subject());

            boolean html = Boolean.TRUE.equals(request.html());

            helper.setText(request.body(), html);

            mailSender.send(message);

        } catch (MessagingException | MailException e) {
            throw new IllegalStateException("Error sending email", e);
        }
    }
}