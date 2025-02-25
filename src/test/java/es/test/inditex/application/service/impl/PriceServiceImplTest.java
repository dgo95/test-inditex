package es.test.inditex.application.service.impl;

import es.test.inditex.adapter.rest.dto.PriceResponse;
import es.test.inditex.domain.entity.Price;
import es.test.inditex.domain.exception.PriceNotFoundException;
import es.test.inditex.domain.repository.PriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PriceServiceImplTest {

    @Mock
    private PriceRepository priceRepository;

    @InjectMocks
    private PriceServiceImpl priceService;

    private Price price1, price2, price3, price4;

    @BeforeEach
    void setUp() {
        // Datos de ejemplo
        price1 = new Price(1L, 1L, LocalDateTime.parse("2020-06-14T00:00:00"),
                LocalDateTime.parse("2020-12-31T23:59:59"), 1, 35455L, 0, new BigDecimal("35.50"), "EUR");
        price2 = new Price(2L, 1L, LocalDateTime.parse("2020-06-14T15:00:00"),
                LocalDateTime.parse("2020-06-14T18:30:00"), 2, 35455L, 1, new BigDecimal("25.45"), "EUR");
        price3 = new Price(3L, 1L, LocalDateTime.parse("2020-06-15T00:00:00"),
                LocalDateTime.parse("2020-06-15T11:00:00"), 3, 35455L, 1, new BigDecimal("30.50"), "EUR");
        price4 = new Price(4L, 1L, LocalDateTime.parse("2020-06-15T16:00:00"),
                LocalDateTime.parse("2020-12-31T23:59:59"), 4, 35455L, 1, new BigDecimal("38.95"), "EUR");
    }

    @Test
    void testGetPrice_Test1() {
        LocalDateTime date = LocalDateTime.parse("2020-06-14T10:00:00");
        Long productId = 35455L;
        Long brandId = 1L;

        when(priceRepository.findTopByProductIdAndBrandIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByPriorityDesc(
                productId, brandId, date, date)).thenReturn(Optional.of(price1));

        PriceResponse response = priceService.getPrice(date, productId, brandId);

        assertEquals(productId, response.productId());
        assertEquals(brandId, response.brandId());
        assertEquals(1, response.priceList());
        assertEquals(new BigDecimal("35.50"), response.price());
        assertEquals(LocalDateTime.parse("2020-06-14T00:00:00"), response.startDate());
        assertEquals(LocalDateTime.parse("2020-12-31T23:59:59"), response.endDate());
    }

    @Test
    void testGetPrice_Test2() {
        LocalDateTime date = LocalDateTime.parse("2020-06-14T16:00:00");
        Long productId = 35455L;
        Long brandId = 1L;

        when(priceRepository.findTopByProductIdAndBrandIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByPriorityDesc(
                productId, brandId, date, date)).thenReturn(Optional.of(price2));

        PriceResponse response = priceService.getPrice(date, productId, brandId);

        assertEquals(productId, response.productId());
        assertEquals(brandId, response.brandId());
        assertEquals(2, response.priceList());
        assertEquals(new BigDecimal("25.45"), response.price());
        assertEquals(LocalDateTime.parse("2020-06-14T15:00:00"), response.startDate());
        assertEquals(LocalDateTime.parse("2020-06-14T18:30:00"), response.endDate());
    }

    @Test
    void testGetPrice_Test3() {
        LocalDateTime date = LocalDateTime.parse("2020-06-14T21:00:00");
        Long productId = 35455L;
        Long brandId = 1L;

        when(priceRepository.findTopByProductIdAndBrandIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByPriorityDesc(
                productId, brandId, date, date)).thenReturn(Optional.of(price1));

        PriceResponse response = priceService.getPrice(date, productId, brandId);

        assertEquals(productId, response.productId());
        assertEquals(brandId, response.brandId());
        assertEquals(1, response.priceList());
        assertEquals(new BigDecimal("35.50"), response.price());
        assertEquals(LocalDateTime.parse("2020-06-14T00:00:00"), response.startDate());
        assertEquals(LocalDateTime.parse("2020-12-31T23:59:59"), response.endDate());
    }

    @Test
    void testGetPrice_Test4() {
        LocalDateTime date = LocalDateTime.parse("2020-06-15T10:00:00");
        Long productId = 35455L;
        Long brandId = 1L;

        when(priceRepository.findTopByProductIdAndBrandIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByPriorityDesc(
                productId, brandId, date, date)).thenReturn(Optional.of(price3));

        PriceResponse response = priceService.getPrice(date, productId, brandId);

        assertEquals(productId, response.productId());
        assertEquals(brandId, response.brandId());
        assertEquals(3, response.priceList());
        assertEquals(new BigDecimal("30.50"), response.price());
        assertEquals(LocalDateTime.parse("2020-06-15T00:00:00"), response.startDate());
        assertEquals(LocalDateTime.parse("2020-06-15T11:00:00"), response.endDate());
    }

    @Test
    void testGetPrice_Test5() {
        LocalDateTime date = LocalDateTime.parse("2020-06-16T21:00:00");
        Long productId = 35455L;
        Long brandId = 1L;

        when(priceRepository.findTopByProductIdAndBrandIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByPriorityDesc(
                productId, brandId, date, date)).thenReturn(Optional.of(price4));

        PriceResponse response = priceService.getPrice(date, productId, brandId);

        assertEquals(productId, response.productId());
        assertEquals(brandId, response.brandId());
        assertEquals(4, response.priceList());
        assertEquals(new BigDecimal("38.95"), response.price());
        assertEquals(LocalDateTime.parse("2020-06-15T16:00:00"), response.startDate());
        assertEquals(LocalDateTime.parse("2020-12-31T23:59:59"), response.endDate());
    }

    @Test
    void testGetPrice_NotFound() {
        LocalDateTime date = LocalDateTime.parse("2020-06-13T10:00:00");
        Long productId = 35455L;
        Long brandId = 1L;

        when(priceRepository.findTopByProductIdAndBrandIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByPriorityDesc(
                productId, brandId, date, date)).thenReturn(Optional.empty());

        assertThrows(PriceNotFoundException.class, () -> priceService.getPrice(date, productId, brandId));
    }
}