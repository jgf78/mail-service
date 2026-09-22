package com.julian.mail_service.kafka;

public record EmailAttachmentEvent(

        String filename,

        String contentType,

        Long size,

        byte[] content

) {
}

