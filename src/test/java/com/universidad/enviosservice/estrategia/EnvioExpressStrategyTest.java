package com.universidad.enviosservice.estrategia;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EnvioExpressStrategyTest {

    private final EnvioExpressStrategy estrategia = new EnvioExpressStrategy();

    @ParameterizedTest
    @CsvSource({
        "3.0, 100.0, 156.0",    // pesoKg <= 5
        "8.0, 100.0, 161.85",   // 5 < pesoKg <= 20
        "25.0, 100.0, 198.25"   // pesoKg > 20
    })
    void calculaCostoPorTramoDePeso(double pesoKg, double distanciaKm, double esperado) {
        assertEquals(esperado, estrategia.calcular(pesoKg, distanciaKm), 0.01);
    }

    @ParameterizedTest
    @CsvSource({
        "3.0, 500.0, 780.0",   // en el limite no hay recargo
        "3.0, 600.0, 968.5"    // distanciaKm > 500 suma 25 antes del factor 1.3
    })
    void aplicaRecargoSoloPorEncimaDe500Km(double pesoKg, double distanciaKm, double esperado) {
        assertEquals(esperado, estrategia.calcular(pesoKg, distanciaKm), 0.01);
    }
}
