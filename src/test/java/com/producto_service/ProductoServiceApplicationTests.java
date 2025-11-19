package com.producto_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ProductoServiceApplicationTests {

    @Test
    void contextLoads() {
        // Test pasa si el contexto de Spring carga correctamente
    }
}
