package com.julian.mail_service.dto;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Detailed information about a stored email")
public record EmailDetailResponse(

        @Schema(example = "1")
        Long id,

        @Schema(example = "SENT")
        String status,

        @Schema(example = "Mail Service test")
        String subject,

        @Schema(example = "Hello, this is a test email.")
        String body,

        @Schema(example = "false")
        Boolean html,

        @Schema(example = "julian.rss.android@gmail.com")
        String replyTo,

        @Schema(example = "2026-09-21T08:49:24.249518")
        LocalDateTime createdAt,

        @Schema(example = "2026-09-21T08:49:31.923031")
        LocalDateTime sentAt,

        String errorMessage,

        List<EmailRecipientResponse> recipients,

        List<EmailAttachmentResponse> attachments
) {
}