package com.julian.mail_service.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.julian.mail_service.dto.EmailMultipartRequest;
import com.julian.mail_service.dto.EmailRequest;
import com.julian.mail_service.dto.EmailResponse;
import com.julian.mail_service.service.EmailSenderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/emails")
@Tag(name = "Emails", description = "Operations for sending emails")
public class EmailController {

    private static final String SEND_EMAIL_SUMMARY = "Send an email";
    private static final String SEND_EMAIL_DESCRIPTION = "Sends an email with optional CC, BCC, reply-to and attachments.";

    private final EmailSenderService emailSenderService;

    public EmailController(EmailSenderService emailSenderService) {
        this.emailSenderService = emailSenderService;
    }

    @Operation(summary = SEND_EMAIL_SUMMARY, description = SEND_EMAIL_DESCRIPTION, requestBody = @RequestBody(required = true, content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(implementation = EmailMultipartRequest.class), encoding = {
            @Encoding(name = "email", contentType = MediaType.APPLICATION_JSON_VALUE),
            @Encoding(name = "attachments", contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE) })))
    @ApiResponse(responseCode = "200", description = "Email sent successfully", content = @Content(schema = @Schema(implementation = EmailResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "500", description = "Error sending email")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EmailResponse> sendEmail(

            @RequestPart("email") @Valid EmailRequest request,

            @RequestPart(value = "attachments", required = false) MultipartFile[] attachments) {

        emailSenderService.send(request, attachments);

        return ResponseEntity.ok(new EmailResponse("SENT", "Email sent successfully"));
    }
}