# The Grand Data Structures Project

**Leer en:** [English](README.md) | [Português](README.pt-BR.md) | [Español](README.es.md)

Un proyecto Java modular que demuestra las estructuras de datos clásicas enseñadas en un curso
universitario de Ciencias de la Computación — un módulo Gradle por estructura, cada uno con su
propio README, una implementación desde cero, una segunda implementación que aplica esa
estructura a un escenario real, y un microbenchmark JMH que convierte la afirmación de
complejidad (Big-O) del libro de texto en un número medido y reproducible. Todo es JVM puro:
sin demo alojada, sin servicios externos, `./gradlew build` y listo.

Este es un proyecto de portafolio de [Leon Lourenço](https://github.com/leon-lourenco),
ingeniero backend sénior, construido en público en lotes acotados. Es hermano del
[design-patterns-project](https://github.com/leon-lourenco/design-patterns-project) del mismo
autor — mismas convenciones, mismo autor, un fundamento distinto: estructuras de datos y sus
trade-offs de complejidad, en lugar de los patrones GoF.

## Algunos números reales

Cada afirmación de abajo está copiada literalmente de una ejecución local real de JMH/JaCoCo —
ver el README de cada módulo para la tabla completa y cómo reproducirla.

- **[Hash Table](hashing/hash-table)**, con cada clave forzada a colisionar en el mismo
  bucket: `get()` pasa de 134 ns a 179.653 ns a medida que la tabla crece de 100 a 100.000
  entradas. Con hashing uniforme, el costo se mantiene fijo en ~3,7 ns todo el tiempo. Misma
  tabla, mismo código — la única variable es la calidad del `hashCode()` de la clave.
- **[AVL Tree](trees/avl-tree)** vs. **[Binary Search Tree](trees/binary-search-tree)**, con la
  misma secuencia de inserción ordenada de 100 claves: la altura del BST simple degenera a
  **100**; el AVL Tree se mantiene en **7**.
- **[Union-Find](graphs/union-find)** con compresión de caminos + unión por rango, frente a la
  versión sin esas optimizaciones, bajo la misma entrada de peor caso: **~3.046x** más rápido
  con 10.000 elementos.

## Por qué classic + applied + benchmark

Una estructura implementada desde cero demuestra que entiendes su mecánica —
redimensionamiento, encadenamiento, rotaciones, recorrido. No demuestra que sabes *cuándo*
recurrir a ella en lugar de la alternativa, ni que la afirmación de Big-O del libro de texto
realmente se sostiene en una JVM real. Por eso cada módulo lleva tres cosas en lugar de una:

- **classic/** — la estructura en sí, hecha a mano (sin depender del tipo equivalente de
  `java.util` como atajo), con pruebas que ejercitan sus casos límite reales (colisiones, orden
  de inserción degenerado, rebalanceo, resize/rehash).
- **applied/** — la misma estructura resolviendo un escenario real, elegido preguntando: ¿cuál
  es el problema real que resuelve esta estructura, y dónde apareció exactamente ese problema?
  El mapeo no es solo fintech por defecto — se toma deliberadamente de donde, en la trayectoria
  del autor (pagos, seguros, telecomunicaciones, modernización de mainframe), el problema
  subyacente encaja mejor.
- **jmh/** — un microbenchmark JMH que mide la operación sobre la que trata la afirmación de
  complejidad del módulo, normalmente como un A/B directo: O(1) vs O(n), caso promedio vs peor
  caso, balanceado vs degenerado. Los números citados en cada README están copiados de una
  ejecución local real, no estimados.

## Las 16 estructuras

Todas completas: implementación classic/applied/benchmark, README propio, y cobertura genuina
del 100% de instrucciones + branches en JaCoCo (no inflada para alcanzar el número — varios
módulos encontraron y corrigieron una brecha real, o simplificaron una rama defensiva
demostrablemente inalcanzable, en su lugar).

| Estructura | Categoría | Escenario aplicado |
|-----------|----------|-------------------|
| [Dynamic Array](linear/dynamic-array) | Linear | Buffer de ingesta por lotes de seguros (aseguradora) |
| [Linked List](linear/linked-list) | Linear | Etapas del flujo de siniestros de seguros (aseguradora) |
| [Stack](linear/stack) | Linear | Validador de corchetes de copybook COBOL legado (banco legado) |
| [Queue / Deque](linear/queue-deque) | Linear | Triaje de tickets de soporte con vía rápida VIP (telecom) |
| [Skip List](linear/skip-list) | Linear | Índice de ventana de rate-limiter |
| [Binary Search Tree](trees/binary-search-tree) | Trees | Consulta de nivel de límite PIX del BACEN |
| [AVL Tree](trees/avl-tree) | Trees | Índice de reglas de fraude (plataforma antifraude) |
| [Heap / Priority Queue](trees/heap) | Trees | Cola de escalamiento por SLA (telecom) |
| [Trie](trees/trie) | Trees | Autocompletado de prefijo de clave PIX (BACEN) |
| [B-Tree](trees/b-tree) | Trees | Simulación de índice de base de datos relacional (banco legado) |
| [Hash Table](hashing/hash-table) | Hashing | Caché de clave de idempotencia PIX |
| [Bloom Filter](hashing/bloom-filter) | Hashing | Pre-chequeo de lista de bloqueo/fraude (aseguradora/plataforma antifraude) |
| [Graph (BFS/DFS)](graphs/graph-bfs-dfs) | Graphs | Recorrido de red para AML (equipo de fraude/compliance) |
| [Dijkstra](graphs/dijkstra) | Graphs | Ruteo de liquidación interbancaria más barato |
| [Union-Find](graphs/union-find) | Graphs | Detección de clústeres de fraude (plataforma antifraude) |
| [Minimum Spanning Tree](graphs/minimum-spanning-tree) | Graphs | Planificación de backhaul de torres celulares (telecom) |

## Estructura

Cada módulo de estructura sigue el mismo esqueleto:

```
<category>/<structure>/
├── build.gradle.kts          # presente solo cuando el módulo necesita dependencias extra
├── README.md                 # problema, solución, complejidad, ambos ejemplos, benchmark, cobertura
└── src/
    ├── main/java/com/datastructures/<category>/<structure>/
    │   ├── classic/           # la implementación desde cero
    │   └── applied/           # el uso en el escenario real
    ├── test/java/...          # refleja la división classic/applied
    └── jmh/java/com/datastructures/<category>/<structure>/benchmark/
        └── ...                # microbenchmark(s) JMH que prueban la afirmación de complejidad empíricamente
```

## Stack técnico

Java 26, Gradle 9.7 (Kotlin DSL, wrapper incluido — `./gradlew` funciona sin instalar Gradle),
JUnit 5, AssertJ, JaCoCo 0.8.15, JMH 1.37. Sin Spring, sin framework — cada módulo es Java
puro, ya que el punto es la estructura de datos, no un contenedor.

**Nota sobre la integración de JMH:** el plugin de Gradle de la comunidad `me.champeau.jmh`
tuvo su último lanzamiento (0.7.3, enero de 2025) probado solo hasta Gradle 8.10/Java 21. En
lugar de pelear contra un plugin desactualizado usando Gradle 9.7/Java 26, el `src/jmh/java` de
cada módulo se conecta directamente como un source set de Gradle plano (ver el
`build.gradle.kts` raíz), con el propio annotation processor de JMH generando las clases de
ejecución del benchmark — ningún plugin de terceros de por medio.

## Ejecutarlo

```bash
./gradlew build                                              # compila todos los módulos
./gradlew test                                                # ejecuta las pruebas de todos los módulos
./gradlew :trees:binary-search-tree:jacocoTestReport          # reporte de cobertura por módulo (HTML)
./gradlew :trees:binary-search-tree:jmh                       # ejecución del benchmark JMH por módulo
```

Sin Docker, sin base de datos, sin llamadas de red — cada prueba y benchmark corre contra
código en proceso. Los números de cobertura y benchmark citados en el README de cada módulo
están copiados de una ejecución local real (JDK 26.0.2 en esta máquina), no estimados.

## Licencia

MIT — ver [LICENSE](LICENSE).
