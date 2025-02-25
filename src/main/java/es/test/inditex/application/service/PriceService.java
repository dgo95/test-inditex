package es.test.inditex.application.service;

import es.test.inditex.adapter.rest.dto.PriceResponse;

import java.time.LocalDateTime;

public interface PriceService {
    PriceResponse getPrice(LocalDateTime date, Long productId, Long brandId);
}
