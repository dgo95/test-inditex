package es.test.inditex.domain.ports;

import es.test.inditex.domain.model.Price;
import java.time.LocalDateTime;
import java.util.Optional;

public interface PriceRepositoryPort {
    Optional<Price> findTopPrice(Long productId, Long brandId, LocalDateTime date);
}