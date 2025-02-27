package es.test.inditex.application.service.impl;

import es.test.inditex.adapter.rest.dto.PriceResponse;
import es.test.inditex.application.service.PriceService;

import es.test.inditex.domain.exception.PriceNotFoundException;
import es.test.inditex.domain.ports.PriceRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PriceServiceImpl implements PriceService {
    private final PriceRepositoryPort priceRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "prices", key = "#productId + '-' + #brandId + '-' + #date")
    public PriceResponse getPrice(LocalDateTime date, Long productId, Long brandId) {
        log.info("Buscando precio para productId: {}, brandId: {} en fecha: {}", productId, brandId, date);
        long startTime = System.currentTimeMillis();
        var price = priceRepositoryPort.findTopPrice(
                        productId, brandId, date)
                .orElseThrow(() -> new PriceNotFoundException("No price found for the given parameters."));
        log.info("Consulta completada en {} ms", System.currentTimeMillis() - startTime);

        return new PriceResponse(
                price.getProductId(),
                price.getBrandId(),
                price.getPriceList(),
                price.getStartDate(),
                price.getEndDate(),
                price.getPrice()
        );
    }
}
