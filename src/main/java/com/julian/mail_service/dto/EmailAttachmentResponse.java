package com.julian.mail_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Email attachment metadata")
public record EmailAttachmentResponse(

        @Schema(example = "1")
        Long id,

        @Schema(example = "document.pdf")
        String filename,

        @Schema(example = "application/pdf")
        String contentType,

        @Schema(example = "24576")
        Long size
) {
}