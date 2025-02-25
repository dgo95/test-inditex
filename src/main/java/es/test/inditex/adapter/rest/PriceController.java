package es.test.inditex.adapter.rest;

import es.test.inditex.adapter.rest.dto.PriceResponse;
import es.test.inditex.application.service.PriceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/prices")
@RequiredArgsConstructor
public class PriceController {

    private final PriceService priceService;

    @GetMapping
    public ResponseEntity<PriceResponse> getPrice(
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date,
            @RequestParam("productId") Long productId,
            @RequestParam("brandId") Long brandId) {

        log.info("Recibiendo petición en el endpoint con date: {}, productId: {}, brandId: {}",
                date, productId, brandId);
        var price = priceService.getPrice(date, productId, brandId);
        return ResponseEntity.ok(price);
    }
}

