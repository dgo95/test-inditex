package es.test.inditex.application.service;

import es.test.inditex.domain.entity.Price;

import java.time.LocalDateTime;

public interface PriceService {
    Price getPrice(LocalDateTime date, Long productId, Long brandId);
}
