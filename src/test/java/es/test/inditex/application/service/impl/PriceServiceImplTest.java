package es.test.inditex.application.service.impl;

import es.test.inditex.adapter.rest.dto.PriceResponse;
import es.test.inditex.domain.model.Price;
import es.test.inditex.domain.exception.PriceNotFoundException;
import es.test.inditex.domain.ports.PriceRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;
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
    private PriceRepositoryPort priceRepositoryPort;

    @InjectMocks
    private PriceServiceImpl priceService;

    private Price price1, price2, price3, price4;

    @BeforeEach
    void setUp() {
        price1 = new Price(1L, 1L, LocalDateTime.parse("2020-06-14T00:00:00"),
                LocalDateTime.parse("2020-12-31T23:59:59"), 1, 35455L, 0, new BigDecimal("35.50"), "EUR");
        price2 = new Price(2L, 1L, LocalDateTime.parse("2020-06-14T15:00:00"),
                LocalDateTime.parse("2020-06-14T18:30:00"), 2, 35455L, 1, new BigDecimal("25.45"), "EUR");
        price3 = new Price(3L, 1L, LocalDateTime.parse("2020-06-15T00:00:00"),
                LocalDateTime.parse("2020-06-15T11:00:00"), 3, 35455L, 1, new BigDecimal("30.50"), "EUR");
        price4 = new Price(4L, 1L, LocalDateTime.parse("2020-06-15T16:00:00"),
                LocalDateTime.parse("2020-12-31T23:59:59"), 4, 35455L, 1, new BigDecimal("38.95"), "EUR");
    }

    @ParameterizedTest
    @CsvSource({
            "2020-06-14T10:00:00, 35455, 1, 1, 35.50, 2020-06-14T00:00:00, 2020-12-31T23:59:59", // Test 1
            "2020-06-14T16:00:00, 35455, 1, 2, 25.45, 2020-06-14T15:00:00, 2020-06-14T18:30:00", // Test 2
            "2020-06-14T21:00:00, 35455, 1, 1, 35.50, 2020-06-14T00:00:00, 2020-12-31T23:59:59", // Test 3
            "2020-06-15T10:00:00, 35455, 1, 3, 30.50, 2020-06-15T00:00:00, 2020-06-15T11:00:00", // Test 4
            "2020-06-16T21:00:00, 35455, 1, 4, 38.95, 2020-06-15T16:00:00, 2020-12-31T23:59:59"  // Test 5
    })
    void testGetPrice_Success(String date, Long productId, Long brandId, int priceList, String price,
                              String startDate, String endDate) {
        LocalDateTime requestDate = LocalDateTime.parse(date);
        Price mockPrice;
        if (priceList == 1) mockPrice = price1;
        else if (priceList == 2) mockPrice = price2;
        else if (priceList == 3) mockPrice = price3;
        else mockPrice = price4;

        when(priceRepositoryPort.findTopPrice(productId, brandId, requestDate)).thenReturn(Optional.of(mockPrice));

        PriceResponse response = priceService.getPrice(requestDate, productId, brandId);

        assertEquals(productId, response.productId());
        assertEquals(brandId, response.brandId());
        assertEquals(priceList, response.priceList());
        assertEquals(new BigDecimal(price), response.price());
        assertEquals(LocalDateTime.parse(startDate), response.startDate());
        assertEquals(LocalDateTime.parse(endDate), response.endDate());
    }

    @Test
    void testGetPrice_NotFound() {
        LocalDateTime date = LocalDateTime.parse("2020-06-13T10:00:00");
        Long productId = 35455L;
        Long brandId = 1L;

        when(priceRepositoryPort.findTopPrice(productId, brandId, date)).thenReturn(Optional.empty());

        assertThrows(PriceNotFoundException.class, () -> priceService.getPrice(date, productId, brandId));
    }
}