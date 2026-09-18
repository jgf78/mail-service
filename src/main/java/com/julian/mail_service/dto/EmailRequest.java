package com.julian.mail_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(description = "Request used to send an email")
public record EmailRequest(

        @Schema(description = "Main recipients", example = "[\"user@example.com\"]") @NotEmpty(message = "The 'to' field must contain at least one recipient") List<@Email(message = "Invalid email address") String> to,

        @Schema(description = "Carbon copy recipients", example = "[\"copy@example.com\"]") List<@Email(message = "Invalid email address") String> cc,

        @Schema(description = "Blind carbon copy recipients", example = "[\"hidden@example.com\"]") List<@Email(message = "Invalid email address") String> bcc,

        @Schema(description = "Address to use for replies", example = "support@example.com") @Email(message = "Invalid reply-to email address") String replyTo,

        @Schema(description = "Email subject", example = "Mail Service test") @NotBlank(message = "The subject is required") String subject,

        @Schema(description = "Email body. Can contain HTML when html is true", example = "Hello, this is a test email.") String body,

        @Schema(description = "Indicates whether the body should be interpreted as HTML", example = "false", defaultValue = "false") Boolean html

) {
}
