package es.test.inditex.adapter.rest.exception;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class GlobalExceptionHandlerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @TestConfiguration
    static class DummyControllerConfig {
        @RestController
        public static class DummyController {
            @GetMapping("/api/test/generic-error")
            public String throwGenericException() {
                // Forced exception to test the generic handler
                throw new RuntimeException("Forced exception to test the generic handler");
            }

            @GetMapping("/api/test/constraint")
            public String testConstraint(@RequestParam @NotNull Long id) {
                return "OK" + id;
            }

            // Endpoint to trigger a HandlerMethodValidationException
            @GetMapping("/api/test/validation")
            public String testValidation(@RequestParam @Min(1) Long id) {
                return "OK" + id;
            }
        }
    }

    @Test
    void testGenericExceptionHandler() throws Exception {
        mockMvc.perform(get("/api/test/generic-error")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.message").value("Internal server error."))
                .andExpect(jsonPath("$.details").value("An unexpected error occurred. Please contact support."));
    }

    @Test
    void testConstraintViolationException() throws Exception {
        mockMvc.perform(get("/api/test/constraint")
                        .contentType(MediaType.APPLICATION_JSON)) // No 'id' parameter provided
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("MISSING_PARAMETER"))
                .andExpect(jsonPath("$.message").value("Missing required parameter: id"))
                .andExpect(jsonPath("$.details").value("Ensure all required parameters are provided."));
    }

    @Test
    void testHandlerMethodValidationException() throws Exception {
        mockMvc.perform(get("/api/test/validation")
                        .param("id", "0") // Invalid value as per @Min(1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Validation error: must be greater than or equal to 1"))
                .andExpect(jsonPath("$.details").value("Ensure that all validation constraints are met."));
    }
}
