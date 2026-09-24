package com.julian.mail_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "email_attachment")
@Getter
@Setter
@NoArgsConstructor
public class EmailAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;

    private String contentType;

    private Long size;

    private String storageKey;

    @ManyToOne
    private Email email;

    public EmailAttachment(
            String filename,
            String contentType,
            Long size,
            String storageKey) {

        this.filename = filename;
        this.contentType = contentType;
        this.size = size;
        this.storageKey = storageKey;
    }
}