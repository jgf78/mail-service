package com.julian.mail_service.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "email")
@Getter
@Setter
@NoArgsConstructor
public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EmailStatus status;

    @Column(nullable = false, length = 500)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String body;

    @Column(nullable = false)
    private Boolean html = false;

    @Column(name = "reply_to", length = 320)
    private String replyTo;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @OneToMany(
            mappedBy = "email",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<EmailRecipient> recipients = new ArrayList<>();

    @OneToMany(
            mappedBy = "email",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<EmailAttachment> attachments = new ArrayList<>();

    public void addRecipient(EmailRecipient recipient) {
        recipients.add(recipient);
        recipient.setEmail(this);
    }

    public void addAttachment(EmailAttachment attachment) {
        attachments.add(attachment);
        attachment.setEmail(this);
    }
}
