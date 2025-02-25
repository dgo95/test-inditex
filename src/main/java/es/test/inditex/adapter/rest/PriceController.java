package es.test.inditex.adapter.rest;

import es.test.inditex.application.service.PriceService;
import es.test.inditex.domain.entity.Price;
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
    public ResponseEntity<Price> getPrice(
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date,
            @RequestParam("productId") Long productId,
            @RequestParam("brandId") Long brandId) {

        log.info("Recibiendo petición en el endpoint con date: {}, productId: {}, brandId: {}",
                date, productId, brandId);
        Price price = priceService.getPrice(date, productId, brandId);
        return ResponseEntity.ok(price);
    }
}

