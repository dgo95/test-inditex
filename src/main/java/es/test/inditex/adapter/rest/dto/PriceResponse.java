package es.test.inditex.adapter.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Respuesta con el precio aplicable para un producto y marca")
public record PriceResponse(
        @Schema(description = "ID del producto", example = "35455")
        Long productId,
        @Schema(description = "ID de la marca", example = "1")
        Long brandId,
        @Schema(description = "Identificador de la tarifa de precios", example = "1")
        Integer priceList,
        @Schema(description = "Fecha de inicio de aplicación", example = "2020-06-14T00:00:00")
        LocalDateTime startDate,
        @Schema(description = "Fecha de fin de aplicación", example = "2020-12-31T23:59:59")
        LocalDateTime endDate,
        @Schema(description = "Precio final aplicable", example = "35.50")
        BigDecimal price
) {
}