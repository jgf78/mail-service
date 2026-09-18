package com.julian.mail_service.service.impl;

import java.io.IOException;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    public void send(EmailRequest request, MultipartFile[] attachments) {

        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            setRecipients(helper, request);
            setContent(helper, request);
            addAttachments(helper, attachments);

            mailSender.send(message);

        } catch (MessagingException | IOException | MailException e) {
            throw new IllegalStateException("Error sending email", e);
        }
    }

    private void setRecipients(MimeMessageHelper helper, EmailRequest request) throws MessagingException {

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
    }

    private void setContent(MimeMessageHelper helper, EmailRequest request) throws MessagingException {

        helper.setSubject(request.subject());

        boolean html = Boolean.TRUE.equals(request.html());

        helper.setText(request.body(), html);
    }

    private void addAttachments(MimeMessageHelper helper, MultipartFile[] attachments)
            throws MessagingException, IOException {

        if (attachments == null || attachments.length == 0) {
            return;
        }

        for (MultipartFile attachment : attachments) {

            if (attachment.isEmpty()) {
                continue;
            }

            String filename = attachment.getOriginalFilename();

            if (filename == null || filename.isBlank()) {
                filename = "attachment";
            }

            byte[] content = attachment.getBytes();

            String contentType = attachment.getContentType();

            if (contentType == null || contentType.isBlank()) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            helper.addAttachment(filename, new ByteArrayResource(content), contentType);
        }
    }
}