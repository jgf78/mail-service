package com.julian.mail_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response returned when an error occurs")
public record ErrorResponse(

        @Schema(description = "Error status", example = "ERROR") String status,

        @Schema(description = "Human-readable error message", example = "Error sending email") String message) {
}