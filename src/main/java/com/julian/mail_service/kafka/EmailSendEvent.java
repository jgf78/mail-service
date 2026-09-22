package com.julian.mail_service.kafka;

import java.util.List;

public record EmailSendEvent(
        List<String> to,
        List<String> cc,
        List<String> bcc,
        String replyTo,
        String subject,
        String body,
        Boolean html,
        List<EmailAttachmentEvent> attachments
) {
}