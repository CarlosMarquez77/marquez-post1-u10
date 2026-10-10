# Envios Service — Análisis de Métricas de Calidad

Post-contenido de la Unidad 10 de Patrones de Diseño de Software.
Proyecto Maven (`envios-service`) que calcula el costo de un envío
según el método de transporte, el peso, la distancia, la fragilidad
y el tipo de cliente. En esta primera parte se analiza con SonarQube
y JaCoCo, sin corregir nada todavía.

## Entorno de análisis
**Decisión de diseño 1 — SonarQube local con Docker.**

- *Control total de la configuración.* La Parte 2 pide duplicar el
  perfil "Sonar way", activar la regla S1541 con su umbral y crear un
  Quality Gate propio. En el plan gratuito de SonarCloud los perfiles
  y Quality Gates personalizados no están disponibles (solo en los
  planes Team y Enterprise), así que con SonarCloud no habría podido
  terminar el laboratorio.
- *No depende de conectividad ni de límites de un plan.* El análisis
  corre contra `http://localhost:9000`, sin cuenta externa y sin que
  el proyecto quede visible para cualquiera.
- *Costo asumido:* el contenedor consume memoria del equipo y hay que
  dejarlo corriendo durante todo el laboratorio. Para un proyecto de
  una sola clase ese costo es aceptable.

```
docker run -d --name sonarqube -p 9000:9000 -e SONAR_ES_BOOTSTRAP_CHECKS_DISABLE=true sonarqube:community
mvn clean verify sonar:sonar -Dsonar.token=TU_TOKEN
```

**Decisión de diseño 2 — Exclusiones del análisis: ninguna.** El
proyecto tiene una sola clase de producción, `ServicioCalculoEnvio`,
y es lógica de negocio. No hay clase `Main`, ni configuración, ni
DTOs, que son los casos que la guía recomienda excluir porque bajan
el porcentaje de cobertura sin decir nada sobre la calidad del código
de negocio. Excluir algo aquí sería esconder justo el código que se
quiere medir. Si más adelante se agregara una clase de arranque sin
lógica, se excluiría con `sonar.exclusions=**/Main.java`.

## Estado inicial del análisis
| Métrica | Valor real observado |
|---------|----------------------|
| Complejidad ciclomática de calcularCosto() | 20 |
| Complejidad cognitiva de calcularCosto() | 32 |
| Cobertura de líneas de ServicioCalculoEnvio | 23,8 % |
| Cobertura de ramas de ServicioCalculoEnvio | 18,4 % |
| Code Smells reportados | 1 |

El único Code Smell es de la regla `java:S3776` sobre
`calcularCosto()` (línea 5): "Refactor this method to reduce its
Cognitive Complexity from 32 to the 15 allowed", con 22 minutos de
deuda técnica estimada. No se reportaron bugs, vulnerabilidades ni
líneas duplicadas.

## Interpretación
**Qué método concentra el problema.** `ServicioCalculoEnvio` tiene un
solo método, `calcularCosto()`, y ahí está todo: en Measures →
Complexity la clase marca 20 y ese valor completo corresponde al
método. Coincide con la fórmula de la Sección 3.1 de la guía
(CC = 1 + puntos de decisión): 4 por la cadena de métodos de envío,
12 por los tramos de peso y los recargos o límites de distancia
dentro de cada rama, 1 por la fragilidad y 2 por el tipo de cliente.
Según la Tabla 3.2 queda en el tope del rango "Alta" (11–20).

**Qué ramas quedaron sin cubrir.** JaCoCo reporta 7 de 38 ramas
cubiertas (31 sin cubrir). Las dos pruebas iniciales solo recorren
ESTANDAR con peso hasta 5 kg, distancia menor a 500 km, cliente
REGULAR o PREMIUM y paquete no frágil. En el reporte HTML quedan en
rojo:

- los tramos de peso 5–20 kg y más de 20 kg de ESTANDAR, y su recargo
  por más de 500 km;
- las ramas completas de EXPRESS, OVERNIGHT (incluida la excepción por
  más de 300 km) e INTERNACIONAL;
- la excepción de método de envío no soportado;
- el recargo por fragilidad y el descuento CORPORATIVO.

**Por qué coinciden las dos señales.** La complejidad alta y la
cobertura baja apuntan al mismo método porque son el mismo problema
visto de dos formas: un método con CC = 20 necesita al menos 20 casos
de prueba para cubrir sus ramas (Sección 3.3 de la guía) y la suite
solo tiene 2.

**Decisión de diseño 3 — Métrica usada como señal principal:
Cyclomatic Complexity.** Se usó la complejidad ciclomática para elegir
qué intervenir en la Parte 2 por tres razones: dice cuántos caminos
independientes tiene el método, y por lo tanto cuántas pruebas hacen
falta; cada punto que suma es un condicional concreto que se puede
ubicar en el código; y es el número que debe bajar si el refactor
realmente reparte las decisiones.

- *Por qué no Coverage:* el 18,4 % dice que faltan pruebas, pero no
  por qué es difícil escribirlas. Es una consecuencia. Si solo se
  mirara la cobertura, la solución parecería "agregar 18 pruebas" a un
  método que seguiría igual de frágil.
- *Por qué no Cognitive Complexity:* es la métrica con la que
  SonarQube levantó el Code Smell (32 frente a 15) y confirma el mismo
  método, pero penaliza el anidamiento y mide qué tan difícil es de
  leer. No se traduce en un número de casos de prueba, que es lo que
  conecta el diagnóstico con la cobertura.

## Capturas del análisis inicial
![Dashboard SonarQube](docs/sonar-dashboard-inicial.png)
![Reporte JaCoCo](docs/jacoco-inicial.png)
