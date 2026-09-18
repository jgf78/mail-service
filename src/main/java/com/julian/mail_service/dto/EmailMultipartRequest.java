package com.julian.mail_service.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

@Schema(description = "Multipart request used to send an email")
public class EmailMultipartRequest {

    @Schema(description = "Email data", implementation = EmailRequest.class)
    private EmailRequest email;

    @ArraySchema(schema = @Schema(type = "string", format = "binary"), arraySchema = @Schema(description = "Optional email attachments"))
    private MultipartFile[] attachments;

    public EmailRequest getEmail() {
        return email;
    }

    public void setEmail(EmailRequest email) {
        this.email = email;
    }

    public MultipartFile[] getAttachments() {
        return attachments;
    }

    public void setAttachments(MultipartFile[] attachments) {
        this.attachments = attachments;
    }
}