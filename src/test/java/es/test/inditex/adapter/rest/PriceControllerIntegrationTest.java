package es.test.inditex.adapter.rest;

import es.test.inditex.domain.repository.PriceRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "classpath:data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class PriceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private PriceRepository priceRepository;

    /**
     * Test 1: Petición a las 10:00 del día 14 para el producto 35455 de la brand 1 (ZARA)
     * Se espera que se aplique la tarifa con priceList = 1.
     */
    @Test
    void testGetPrice_Test1() throws Exception {
        mockMvc.perform(get("/api/prices")
                        .param("date", "2020-06-14T10:00:00")
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(1))
                .andExpect(jsonPath("$.price").value(35.50))
                .andExpect(jsonPath("$.startDate").value("2020-06-14T00:00:00"))
                .andExpect(jsonPath("$.endDate").value("2020-12-31T23:59:59"));
    }

    /**
     * Test 2: Petición a las 16:00 del día 14 para el producto 35455 de la brand 1 (ZARA)
     * Se espera que se aplique la tarifa con priceList = 2, ya que está en un rango de mayor prioridad.
     */
    @Test
    void testGetPrice_Test2() throws Exception {
        mockMvc.perform(get("/api/prices")
                        .param("date", "2020-06-14T16:00:00")
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(2))
                .andExpect(jsonPath("$.price").value(25.45))
                .andExpect(jsonPath("$.startDate").value("2020-06-14T15:00:00"))
                .andExpect(jsonPath("$.endDate").value("2020-06-14T18:30:00"));
    }

    /**
     * Test 3: Petición a las 21:00 del día 14 para el producto 35455 de la brand 1 (ZARA)
     * Se espera que se aplique la tarifa con priceList = 1, ya que la tarifa 2 ya ha finalizado.
     */
    @Test
    void testGetPrice_Test3() throws Exception {
        mockMvc.perform(get("/api/prices")
                        .param("date", "2020-06-14T21:00:00")
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(1))
                .andExpect(jsonPath("$.price").value(35.50))
                .andExpect(jsonPath("$.startDate").value("2020-06-14T00:00:00"))
                .andExpect(jsonPath("$.endDate").value("2020-12-31T23:59:59"));
    }

    /**
     * Test 4: Petición a las 10:00 del día 15 para el producto 35455 de la brand 1 (ZARA)
     * Se espera que se aplique la tarifa con priceList = 3.
     */
    @Test
    void testGetPrice_Test4() throws Exception {
        mockMvc.perform(get("/api/prices")
                        .param("date", "2020-06-15T10:00:00")
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(3))
                .andExpect(jsonPath("$.price").value(30.50))
                .andExpect(jsonPath("$.startDate").value("2020-06-15T00:00:00"))
                .andExpect(jsonPath("$.endDate").value("2020-06-15T11:00:00"));
    }

    /**
     * Test 5: Petición a las 21:00 del día 16 para el producto 35455 de la brand 1 (ZARA)
     * Se espera que se aplique la tarifa con priceList = 4.
     */
    @Test
    void testGetPrice_Test5() throws Exception {
        mockMvc.perform(get("/api/prices")
                        .param("date", "2020-06-16T21:00:00")
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(4))
                .andExpect(jsonPath("$.price").value(38.95))
                .andExpect(jsonPath("$.startDate").value("2020-06-15T16:00:00"))
                .andExpect(jsonPath("$.endDate").value("2020-12-31T23:59:59"));
    }

    /**
     * Test 6: Petición a las 10:00 del día 14 para el producto 35455 y sin brand
     * Se espera que falle por faltar un parámetro.
     */
    @Test
    void testGetPrice_MissingParam() throws Exception {
        mockMvc.perform(get("/api/prices")
                        .param("date", "2020-06-14T10:00:00")
                        .param("productId", "35455"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Parámetro obligatorio faltante: brandId"));
    }

    /**
     * Test 7: Petición con fecha inválida para el producto 35455 de la brand 1 (ZARA)
     * Se espera que falle por no tener una fecha válida.
     */
    @Test
    void testGetPrice_InvalidDate() throws Exception {
        mockMvc.perform(get("/api/prices")
                        .param("date", "invalid-date")
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Parámetro inválido: date debe ser de tipo LocalDateTime"));
    }

    /**
     * Test 8: Petición que no encuentra un precio.
     * Se espera que se retorne 404 con el mensaje de error del PriceNotFoundException.
     */
    @Test
    void testGetPrice_NotFound() throws Exception {
        mockMvc.perform(get("/api/prices")
                        .param("date", "2020-06-13T10:00:00")
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No se encontró precio para los parámetros indicados."));

    }

}