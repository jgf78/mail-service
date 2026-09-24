package com.julian.mail_service.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.InputStreamSource;
import org.springframework.http.MediaType;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.julian.mail_service.dto.EmailAttachmentResponse;
import com.julian.mail_service.dto.EmailDetailResponse;
import com.julian.mail_service.dto.EmailRecipientResponse;
import com.julian.mail_service.dto.EmailRequest;
import com.julian.mail_service.entity.Email;
import com.julian.mail_service.entity.EmailAttachment;
import com.julian.mail_service.entity.EmailRecipient;
import com.julian.mail_service.entity.EmailStatus;
import com.julian.mail_service.entity.RecipientType;
import com.julian.mail_service.repository.EmailRepository;
import com.julian.mail_service.service.EmailSenderService;
import com.julian.mail_service.service.StorageService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailSenderServiceImpl implements EmailSenderService {

    private final JavaMailSender mailSender;

    private final EmailRepository emailRepository;

    private final EmailSenderService emailSenderService;

    private final StorageService storageService;

    public EmailSenderServiceImpl(
            JavaMailSender mailSender,
            EmailRepository emailRepository,
            @Lazy EmailSenderService emailSenderService,
            StorageService storageService) {

        this.mailSender = mailSender;
        this.emailRepository = emailRepository;
        this.emailSenderService = emailSenderService;
        this.storageService = storageService;
    }

    @Override
    public Long send(
            EmailRequest request,
            MultipartFile[] attachments) {

        Email email = createEmailEntity(request, attachments);

        email.setStatus(EmailStatus.QUEUED);
        email.setCreatedAt(LocalDateTime.now());

        emailRepository.save(email);

        emailSenderService.processEmail(email);

        return email.getId();
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

            try {

                String storageKey = storageService.upload(
                        attachment.getInputStream(),
                        filename,
                        contentType,
                        attachment.getSize()
                );

                email.addAttachment(
                        new EmailAttachment(
                                filename,
                                contentType,
                                attachment.getSize(),
                                storageKey
                        )
                );

            } catch (IOException e) {

                throw new IllegalStateException(
                        "Error reading attachment: " + filename,
                        e
                );
            }
        }
    }

    private void sendEmail(Email email)
            throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper =
                new MimeMessageHelper(
                        message,
                        true,
                        "UTF-8"
                );

        setRecipients(helper, email);
        setContent(helper, email);
        addAttachments(helper, email);

        mailSender.send(message);
    }

    private void setRecipients(
            MimeMessageHelper helper,
            Email email)
            throws MessagingException {

        List<String> to = email.getRecipients().stream()
                .filter(recipient ->
                        recipient.getType() == RecipientType.TO)
                .map(EmailRecipient::getAddress)
                .toList();

        List<String> cc = email.getRecipients().stream()
                .filter(recipient ->
                        recipient.getType() == RecipientType.CC)
                .map(EmailRecipient::getAddress)
                .toList();

        List<String> bcc = email.getRecipients().stream()
                .filter(recipient ->
                        recipient.getType() == RecipientType.BCC)
                .map(EmailRecipient::getAddress)
                .toList();

        helper.setTo(to.toArray(new String[0]));

        if (!cc.isEmpty()) {
            helper.setCc(cc.toArray(new String[0]));
        }

        if (!bcc.isEmpty()) {
            helper.setBcc(bcc.toArray(new String[0]));
        }

        if (email.getReplyTo() != null
                && !email.getReplyTo().isBlank()) {

            helper.setReplyTo(email.getReplyTo());
        }
    }

    private void setContent(
            MimeMessageHelper helper,
            Email email)
            throws MessagingException {

        helper.setSubject(email.getSubject());

        boolean html = Boolean.TRUE.equals(email.getHtml());

        helper.setText(email.getBody(), html);
    }

    private void addAttachments(
            MimeMessageHelper helper,
            Email email)
            throws MessagingException {

        for (EmailAttachment attachment : email.getAttachments()) {

            if (attachment.getStorageKey() == null
                    || attachment.getStorageKey().isBlank()) {
                continue;
            }

            InputStreamSource resource =
                    () -> storageService.download(
                            attachment.getStorageKey()
                    );

            helper.addAttachment(
                    attachment.getFilename(),
                    resource,
                    attachment.getContentType()
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmailDetailResponse> findAll() {

        return emailRepository.findAll()
                .stream()
                .map(this::toEmailDetailResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EmailDetailResponse> findById(Long id) {

        return emailRepository.findById(id)
                .map(this::toEmailDetailResponse);
    }

    private EmailDetailResponse toEmailDetailResponse(
            Email email) {

        List<EmailRecipientResponse> recipients =
                email.getRecipients()
                        .stream()
                        .map(recipient ->
                                new EmailRecipientResponse(
                                        recipient.getId(),
                                        recipient.getAddress(),
                                        recipient.getType()
                                )
                        )
                        .toList();

        List<EmailAttachmentResponse> attachments =
                email.getAttachments()
                        .stream()
                        .map(attachment ->
                                new EmailAttachmentResponse(
                                        attachment.getId(),
                                        attachment.getFilename(),
                                        attachment.getContentType(),
                                        attachment.getSize()
                                )
                        )
                        .toList();

        return new EmailDetailResponse(
                email.getId(),
                email.getStatus().name(),
                email.getSubject(),
                email.getBody(),
                email.getHtml(),
                email.getReplyTo(),
                email.getCreatedAt(),
                email.getSentAt(),
                email.getErrorMessage(),
                recipients,
                attachments
        );
    }

    @Override
    @Transactional(noRollbackFor = IllegalStateException.class)
    public void processEmail(Email email) {

        try {

            email.setStatus(EmailStatus.PROCESSING);

            emailRepository.save(email);

            sendEmail(email);

            email.setStatus(EmailStatus.SENT);
            email.setSentAt(LocalDateTime.now());
            email.setErrorMessage(null);

            emailRepository.save(email);

        } catch (MessagingException | MailException e) {

            email.setStatus(EmailStatus.ERROR);
            email.setErrorMessage(e.getMessage());

            emailRepository.save(email);

            throw new IllegalStateException(
                    "Error sending email",
                    e
            );
        }
    }
}