package es.test.inditex.application.service.impl;

import es.test.inditex.adapter.rest.dto.PriceResponse;
import es.test.inditex.application.service.PriceService;

import es.test.inditex.domain.exception.PriceNotFoundException;
import es.test.inditex.domain.repository.PriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PriceServiceImpl implements PriceService {
    private final PriceRepository priceRepository;

    @Override
    public PriceResponse getPrice(LocalDateTime date, Long productId, Long brandId) {
        log.info("Buscando precio para productId: {}, brandId: {} en fecha: {}", productId, brandId, date);
        var price = priceRepository.findTopByProductIdAndBrandIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByPriorityDesc(
                        productId, brandId, date, date)
                .orElseThrow(() -> new PriceNotFoundException("No se encontró precio para los parámetros indicados."));

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
