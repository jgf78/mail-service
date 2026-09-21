package com.julian.mail_service.dto;

import com.julian.mail_service.entity.RecipientType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Email recipient")
public record EmailRecipientResponse(

        @Schema(example = "1")
        Long id,

        @Schema(example = "julian.rss.android@gmail.com")
        String address,

        @Schema(example = "TO")
        RecipientType type
) {
}