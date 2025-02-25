package es.test.inditex;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class InditexApplicationTest {

    @Test
    void main_runsWithoutException() {
        String[] args = {};
        // Se verifica que al ejecutar el main no se lance ninguna excepción.
        assertDoesNotThrow(() -> InditexApplication.main(args));
    }
}
