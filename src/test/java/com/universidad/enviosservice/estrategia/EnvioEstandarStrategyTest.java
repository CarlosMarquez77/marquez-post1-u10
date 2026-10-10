package com.universidad.enviosservice.estrategia;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EnvioEstandarStrategyTest {

    private final EnvioEstandarStrategy estrategia = new EnvioEstandarStrategy();

    @ParameterizedTest
    @CsvSource({
        "3.0, 100.0, 50.0",    // pesoKg <= 5
        "5.0, 100.0, 50.0",    // limite del primer tramo
        "8.0, 100.0, 52.4",    // 5 < pesoKg <= 20
        "20.0, 100.0, 62.0",   // limite del segundo tramo
        "25.0, 100.0, 68.0"    // pesoKg > 20
    })
    void calculaCostoPorTramoDePeso(double pesoKg, double distanciaKm, double esperado) {
        assertEquals(esperado, estrategia.calcular(pesoKg, distanciaKm), 0.01);
    }

    @ParameterizedTest
    @CsvSource({
        "3.0, 500.0, 250.0",   // en el limite no hay recargo
        "3.0, 600.0, 310.0"    // distanciaKm > 500 suma 10
    })
    void aplicaRecargoSoloPorEncimaDe500Km(double pesoKg, double distanciaKm, double esperado) {
        assertEquals(esperado, estrategia.calcular(pesoKg, distanciaKm), 0.01);
    }
}
