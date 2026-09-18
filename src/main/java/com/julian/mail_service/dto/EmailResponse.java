package com.julian.mail_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response returned after sending an email")
public record EmailResponse(

        @Schema(description = "Email sending status", example = "SENT") String status,

        @Schema(description = "Human-readable result message", example = "Email sent successfully") String message

) {
}