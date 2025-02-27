package es.test.inditex.adapter.rest;

import es.test.inditex.adapter.rest.dto.PriceResponse;
import es.test.inditex.application.service.PriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/prices")
@RequiredArgsConstructor
public class PriceController {

    private final PriceService priceService;

    @Operation(summary = "Obtener el precio aplicable", description = "Devuelve el precio aplicable para un producto y marca en una fecha específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Precio encontrado exitosamente",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PriceResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos o faltantes",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(type = "string", example = "Parámetro inválido: date debe ser de tipo LocalDateTime"))),
            @ApiResponse(responseCode = "404", description = "No se encontró precio para los parámetros indicados",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(type = "string", example = "No se encontró precio para los parámetros indicados.")))
    })
    @GetMapping
    public ResponseEntity<PriceResponse> getPrice(
            @Parameter(description = "Fecha de aplicación en formato ISO (ej. 2020-06-14T10:00:00)", required = true)
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date,
            @Parameter(description = "ID del producto", required = true, example = "35455")
            @NotNull
            @Min(value = 1, message = "productId must be greater than or equal to 1")
            @RequestParam("productId") Long productId,
            @Parameter(description = "ID de la marca", required = true, example = "1")
            @NotNull
            @Min(value = 1, message = "brandId must be greater than or equal to 1")
            @RequestParam("brandId") Long brandId) {

        log.info("Recibiendo petición en el endpoint con date: {}, productId: {}, brandId: {}",
                date, productId, brandId);
        var price = priceService.getPrice(date, productId, brandId);
        return ResponseEntity.ok(price);
    }
}

