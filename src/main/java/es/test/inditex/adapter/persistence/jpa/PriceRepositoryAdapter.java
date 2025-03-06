package es.test.inditex.adapter.persistence.jpa;

import es.test.inditex.domain.model.Price;
import es.test.inditex.domain.ports.PriceRepositoryPort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class PriceRepositoryAdapter implements PriceRepositoryPort {
    private final PriceRepositoryJpa priceRepositoryJpa;

    public PriceRepositoryAdapter(PriceRepositoryJpa priceRepositoryJpa) {
        this.priceRepositoryJpa = priceRepositoryJpa;
    }

    @Override
    public Optional<Price> findTopPrice(Long productId, Long brandId, LocalDateTime date) {
        return priceRepositoryJpa.findTopPrice(
                        productId, brandId, date)
                .map(this::toDomainModel);
    }

    private Price toDomainModel(PriceEntity entity) {
        return new Price(
                entity.getId(),
                entity.getBrandId(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getPriceList(),
                entity.getProductId(),
                entity.getPriority(),
                entity.getPrice(),
                entity.getCurr()
        );
    }
}