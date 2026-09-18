package com.julian.mail_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.julian.mail_service.dto.EmailRequest;
import com.julian.mail_service.dto.EmailResponse;
import com.julian.mail_service.service.EmailSenderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/emails")
@Tag(
        name = "Emails",
        description = "Operations for sending emails"
)
public class EmailController {

    private final EmailSenderService emailSenderService;

    public EmailController(EmailSenderService emailSenderService) {
        this.emailSenderService = emailSenderService;
    }

    @Operation(
            summary = "Send an email",
            description = "Sends an email through the configured SMTP server"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Email sent successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EmailResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid email request"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error sending email"
            )
    })
    @PostMapping
    public ResponseEntity<EmailResponse> sendEmail(
            @Valid @RequestBody EmailRequest request) {

        emailSenderService.send(request);

        return ResponseEntity.ok(
                new EmailResponse(
                        "SENT",
                        "Email sent successfully"
                )
        );
    }
}