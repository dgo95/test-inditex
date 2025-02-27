package es.test.inditex;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class InditexApplicationTests {

    @Test
    void contextLoads() {
        String[] args = {};
        // Se verifica que al ejecutar el main no se lance ninguna excepción.
        assertDoesNotThrow(() -> InditexApplication.main(args));
    }

}
