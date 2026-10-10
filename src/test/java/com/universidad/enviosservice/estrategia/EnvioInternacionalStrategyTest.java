package com.universidad.enviosservice.estrategia;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EnvioInternacionalStrategyTest {

    private final EnvioInternacionalStrategy estrategia = new EnvioInternacionalStrategy();

    @ParameterizedTest
    @CsvSource({
        "3.0, 100.0, 230.0",   // pesoKg <= 5
        "8.0, 100.0, 239.0",   // 5 < pesoKg <= 20
        "25.0, 100.0, 297.5"   // pesoKg > 20
    })
    void calculaCostoPorTramoDePeso(double pesoKg, double distanciaKm, double esperado) {
        assertEquals(esperado, estrategia.calcular(pesoKg, distanciaKm), 0.01);
    }

    @ParameterizedTest
    @CsvSource({
        "3.0, 5000.0, 9050.0",    // en el limite no hay recargo
        "3.0, 6000.0, 10950.0"    // distanciaKm > 5000 suma 100
    })
    void aplicaRecargoSoloPorEncimaDe5000Km(double pesoKg, double distanciaKm, double esperado) {
        assertEquals(esperado, estrategia.calcular(pesoKg, distanciaKm), 0.01);
    }
}
