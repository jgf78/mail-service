package com.julian.mail_service.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.julian.mail_service.dto.EmailRequest;
import com.julian.mail_service.entity.Email;
import com.julian.mail_service.entity.EmailAttachment;
import com.julian.mail_service.entity.EmailRecipient;
import com.julian.mail_service.entity.EmailStatus;
import com.julian.mail_service.entity.RecipientType;
import com.julian.mail_service.repository.EmailRepository;
import com.julian.mail_service.service.EmailSenderService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailSenderServiceImpl implements EmailSenderService {

    private final JavaMailSender mailSender;
    private final EmailRepository emailRepository;

    public EmailSenderServiceImpl(
            JavaMailSender mailSender,
            EmailRepository emailRepository) {

        this.mailSender = mailSender;
        this.emailRepository = emailRepository;
    }

    @Override
    public void send(EmailRequest request, MultipartFile[] attachments) {

        Email email = createEmailEntity(request, attachments);

        email.setStatus(EmailStatus.QUEUED);
        email.setCreatedAt(LocalDateTime.now());

        emailRepository.save(email);

        try {

            email.setStatus(EmailStatus.PROCESSING);
            emailRepository.save(email);

            sendEmail(request, attachments);

            email.setStatus(EmailStatus.SENT);
            email.setSentAt(LocalDateTime.now());

            emailRepository.save(email);

        } catch (MessagingException | IOException | MailException e) {

            email.setStatus(EmailStatus.ERROR);
            email.setErrorMessage(e.getMessage());

            emailRepository.save(email);

            throw new IllegalStateException("Error sending email", e);
        }
    }

    private Email createEmailEntity(
            EmailRequest request,
            MultipartFile[] attachments) {

        Email email = new Email();

        email.setSubject(request.subject());
        email.setBody(request.body());
        email.setHtml(Boolean.TRUE.equals(request.html()));
        email.setReplyTo(request.replyTo());

        addRecipients(email, request);
        addAttachments(email, attachments);

        return email;
    }

    private void addRecipients(
            Email email,
            EmailRequest request) {

        if (request.to() != null) {

            request.to().forEach(address ->
                    email.addRecipient(
                            new EmailRecipient(
                                    address,
                                    RecipientType.TO
                            )
                    )
            );
        }

        if (request.cc() != null) {

            request.cc().forEach(address ->
                    email.addRecipient(
                            new EmailRecipient(
                                    address,
                                    RecipientType.CC
                            )
                    )
            );
        }

        if (request.bcc() != null) {

            request.bcc().forEach(address ->
                    email.addRecipient(
                            new EmailRecipient(
                                    address,
                                    RecipientType.BCC
                            )
                    )
            );
        }
    }

    private void addAttachments(
            Email email,
            MultipartFile[] attachments) {

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

            String contentType = attachment.getContentType();

            if (contentType == null || contentType.isBlank()) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            email.addAttachment(
                    new EmailAttachment(
                            filename,
                            contentType,
                            attachment.getSize()
                    )
            );
        }
    }

    private void sendEmail(
            EmailRequest request,
            MultipartFile[] attachments)
            throws MessagingException, IOException {

        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper =
                new MimeMessageHelper(message, true, "UTF-8");

        setRecipients(helper, request);
        setContent(helper, request);
        addAttachments(helper, attachments);

        mailSender.send(message);
    }

    private void setRecipients(
            MimeMessageHelper helper,
            EmailRequest request)
            throws MessagingException {

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

    private void setContent(
            MimeMessageHelper helper,
            EmailRequest request)
            throws MessagingException {

        helper.setSubject(request.subject());

        boolean html = Boolean.TRUE.equals(request.html());

        helper.setText(request.body(), html);
    }

    private void addAttachments(
            MimeMessageHelper helper,
            MultipartFile[] attachments)
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

            helper.addAttachment(
                    filename,
                    new ByteArrayResource(content),
                    contentType
            );
        }
    }
}