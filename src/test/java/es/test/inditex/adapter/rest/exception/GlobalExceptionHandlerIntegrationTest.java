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
                // Se lanza una excepción genérica para testear el GlobalExceptionHandler.
                throw new RuntimeException("Excepción forzada para testear el handler genérico");
            }

            @GetMapping("/api/test/constraint")
            public String testConstraint(@RequestParam @NotNull Long id) {
                return "OK" + id;
            }

            // Método para provocar HandlerMethodValidationException
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
                .andExpect(content().string("Internal server error."));
    }

    @Test
    void testConstraintViolationException() throws Exception {
        mockMvc.perform(get("/api/test/constraint")
                        .contentType(MediaType.APPLICATION_JSON)) // Sin enviar el parámetro id
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Missing required parameter: id"));
    }

    @Test
    void testHandlerMethodValidationException() throws Exception {
        mockMvc.perform(get("/api/test/validation")
                        .param("id", "0") // Valor inválido según @Min(1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Validation error: must be greater than or equal to 1"));
    }
}