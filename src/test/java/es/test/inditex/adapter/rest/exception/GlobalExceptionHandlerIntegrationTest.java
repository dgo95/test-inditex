package es.test.inditex.adapter.rest.exception;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
        }
    }

    @Test
    void testGenericExceptionHandler() throws Exception {
        mockMvc.perform(get("/api/test/generic-error")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error interno del servidor."));
    }
}
