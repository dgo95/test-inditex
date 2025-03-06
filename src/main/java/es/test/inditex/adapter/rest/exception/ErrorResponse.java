package es.test.inditex.adapter.rest.exception;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Error response structure")
public record ErrorResponse(
        @Schema(description = "Error code", example = "PRICE_NOT_FOUND") String errorCode,
        @Schema(description = "Error message", example = "Price not found for the provided parameters.") String message,
        @Schema(description = "Additional details", example = "Check the values for date, productId, and brandId.") String details
) {}