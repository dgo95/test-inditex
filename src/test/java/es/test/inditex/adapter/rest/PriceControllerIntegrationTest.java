package es.test.inditex.adapter.rest;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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

    @ParameterizedTest
    @CsvSource({
            "2020-06-14T10:00:00, 35455, 1, 1, 35.50, 2020-06-14T00:00:00, 2020-12-31T23:59:59", // Test 1
            "2020-06-14T16:00:00, 35455, 1, 2, 25.45, 2020-06-14T15:00:00, 2020-06-14T18:30:00", // Test 2
            "2020-06-14T21:00:00, 35455, 1, 1, 35.50, 2020-06-14T00:00:00, 2020-12-31T23:59:59", // Test 3
            "2020-06-15T10:00:00, 35455, 1, 3, 30.50, 2020-06-15T00:00:00, 2020-06-15T11:00:00", // Test 4
            "2020-06-16T21:00:00, 35455, 1, 4, 38.95, 2020-06-15T16:00:00, 2020-12-31T23:59:59"  // Test 5
    })
    void testGetPrice_Success(String date, String productId, String brandId, int priceList, double price,
                              String startDate, String endDate) throws Exception {
        mockMvc.perform(get("/api/prices")
                        .param("date", date)
                        .param("productId", productId)
                        .param("brandId", brandId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(productId))
                .andExpect(jsonPath("$.brandId").value(brandId))
                .andExpect(jsonPath("$.priceList").value(priceList))
                .andExpect(jsonPath("$.price").value(price))
                .andExpect(jsonPath("$.startDate").value(startDate))
                .andExpect(jsonPath("$.endDate").value(endDate));
    }

    @ParameterizedTest
    @CsvSource({
            "2020-06-14T10:00:00, 35455, '', Missing required parameter: brandId",
            "2020-06-14T10:00:00, '', 1, Missing required parameter: productId",
            "'', 35455, 1, Missing required parameter: date"
    })
    void testGetPrice_MissingParam(String date, String productId, String brandId, String expectedMessage)
            throws Exception {
        mockMvc.perform(get("/api/prices")
                        .param("date", date == null || date.isEmpty() ? "" : date)
                        .param("productId", productId == null || productId.isEmpty() ? "" : productId)
                        .param("brandId", brandId == null || brandId.isEmpty() ? "" : brandId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedMessage));
    }

    @ParameterizedTest
    @CsvSource({
            "invalid-date, 35455, 1, Invalid parameter: date must be of type LocalDateTime. Provided value: invalid-date",
            "2020-06-14T10:00:00, abc, 1, Invalid parameter: productId must be of type Long. Provided value: abc",
            "2020-06-14T10:00:00, 35455, xyz, Invalid parameter: brandId must be of type Long. Provided value: xyz"
    })
    void testGetPrice_InvalidParam(String date, String productId, String brandId, String expectedMessage)
            throws Exception {
        mockMvc.perform(get("/api/prices")
                        .param("date", date)
                        .param("productId", productId)
                        .param("brandId", brandId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedMessage));
    }

    @ParameterizedTest
    @CsvSource({
            "2020-06-14T10:00:00, 0, 1, Validation error: productId must be greater than or equal to 1",
            "2020-06-14T10:00:00, 35455, 0, Validation error: brandId must be greater than or equal to 1"
    })
    void testGetPrice_ConstraintViolation(String date, String productId, String brandId, String expectedMessage)
            throws Exception {
        mockMvc.perform(get("/api/prices")
                        .param("date", date)
                        .param("productId", productId)
                        .param("brandId", brandId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedMessage));
    }

    @ParameterizedTest
    @CsvSource({
            "2020-06-13T10:00:00, 35455, 1, No price found for the given parameters."
    })
    void testGetPrice_NotFound(String date, String productId, String brandId, String expectedMessage)
            throws Exception {
        mockMvc.perform(get("/api/prices")
                        .param("date", date)
                        .param("productId", productId)
                        .param("brandId", brandId))
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectedMessage));
    }
}