# Preparación para la sustentación

Guía corta del código **actual**. Cada sección termina con preguntas probables y una respuesta de una o dos líneas. Intenta responderlas tú antes de leer la respuesta.

## 0. Decisiones generales

| Decisión | Por qué |
|---|---|
| Una `class` por algoritmo (`CountingInversions`, `QuickSort3`, `ClosestPair`), no `object` | `QuickSort3` guarda su semilla en el constructor: una configuración inmutable. Las otras dos siguen el mismo patrón por consistencia. |
| Sin `var`, sin `map`/`filter`/`fold`, sin funciones anónimas | Lo exige el enunciado. Solo se usan métodos de `List` que no son de alto orden: `splitAt`, `length`, `reverse`, `:::`. |
| `@tailrec` en los recorridos | Son 12 funciones en los algoritmos; el enunciado pide al menos 4. `@tailrec` **no** hace la optimización: le exige al compilador comprobar que la llamada es de cola. |
| `@tailrec` solo en métodos `private` o funciones locales | Un método público de una clase podría sobrescribirse, y entonces el compilador rechaza `@tailrec`. |
| Validación con `require` en `ClosestPair` | Las pruebas y el benchmark necesitan rechazar entradas inválidas, y `distance` debe seguir devolviendo un `Double`, como pide el enunciado. |
| `var sink` eliminado del `Benchmark` | Se reemplazó por un *checksum* que se suma y se imprime al final: la JVM no puede descartar el cálculo y no queda ninguna `var`. |

**¿Por qué agregar al frente y no al final de una lista?** `x :: lista` cuesta O(1); agregar al final copia la lista y cuesta O(n). Por eso los acumuladores se construyen al frente.

---

## 1. Conteo de inversiones (`CountingInversions`)

**Idea:** Merge Sort que cuenta mientras fusiona. `sortAndCount` devuelve `(lista ordenada, inversiones)` y `mergeAndCount` fusiona y cuenta los pares cruzados.

**Clave de la fusión:** si la cabeza izquierda `x` es mayor que la derecha `y`, entonces `y` es menor que **todos** los que quedan a la izquierda (la izquierda está ordenada). Por eso se suma `leftRemaining`, no 1. Si `x <= y`, se toma `x` sin contar: la igualdad no es inversión.

**Traza que debes saber hacer a mano:** `[2, 3]` con `[2, 9, 9]`.

| x | y | Acción | leftRemaining | inversions |
|---|---|---|---|---|
| 2 | 2 | toma x (2 ≤ 2) | 1 | 0 |
| 3 | 2 | toma y, suma 1 | 1 | 1 |
| 3 | 9 | toma x | 0 | 1 |
| — | 9 | izquierda vacía | 0 | 1 |

**Complejidad:** T(n) = 2T(n/2) + Θ(n) → método maestro, caso 2 → **Θ(n log n)** para cualquier orden de entrada.

**Preguntas probables**
- *¿Por qué `sortAndCount` devuelve también la lista?* Porque la fusión solo puede contar si las mitades vienen ordenadas.
- *¿Por qué `leftRemaining` y no `left.length` en cada paso?* `length` recorre la lista: cada paso costaría O(n) y la fusión sería cuadrática. El contador se calcula una vez y baja en 1 al sacar de la izquierda.
- *¿Por qué `Long`?* El máximo es n(n−1)/2. Con n = 100.000 son 4.999.950.000, más que `Int.MaxValue` (2.147.483.647).
- *¿Por qué hay `reverse` en la fusión?* Se toman los elementos de menor a mayor y se agregan al frente, así que el acumulador queda al revés.
- *`sortAndCount` no es `@tailrec`, ¿desborda la pila?* No: siempre divide a la mitad por posición, así que la profundidad es log₂ n ≈ 20 con un millón de elementos.

---

## 2. Quick Sort de tres vías (`QuickSort3`)

**Idea:** se elige un pivote pseudoaleatorio y se parte en `L` (< p), `E` (= p) y `G` (> p). Los iguales ya quedan en su lugar y nunca se vuelven a procesar.

**Cómo está hecho:**
- `partition3`: un solo recorrido con tres acumuladores. No invierte nada, porque el orden dentro de cada grupo no importa.
- `sortWithAcc`: un solo bucle `@tailrec` sobre una lista de grupos pendientes `(lista, listo)`. Un grupo de iguales (`true`) pasa directo a la salida; cualquier otro se parte y se reemplaza por `(G, false), (E, true), (L, false)`.
- **Primero los mayores:** como cada grupo se agrega al frente de `acc`, procesar de mayor a menor deja `acc` ordenado. Así no hay ningún `reverse`.
- **Pivote:** `nextState = state * 1664525 + 1013904223` y el índice es `Math.floorMod(nextState, length)`. La semilla va en el constructor (`20261011` en `Main` y en `Benchmark`).

**Complejidad**

| Caso | Resultado |
|---|---|
| Esperado (pivote aleatorio) | Θ(n log n) |
| Peor caso (pivote siempre mínimo o máximo) | Θ(n²) |
| Todos iguales | Θ(n), una sola partición |
| k valores distintos | O(n·k) |

**Preguntas probables**
- *¿Por qué no usar `head` como pivote?* Con una lista ordenada, `L` queda vacía y `G` con n−1 elementos en cada paso: T(n) = T(n−1) + Θ(n) = Θ(n²). Además, el enunciado pide Quick Sort **aleatorio**.
- *¿Por qué el método maestro no sirve en el peor caso?* Porque el tamaño baja en 1, no se divide por una constante.
- *¿El azar puede dar un resultado incorrecto?* No: cualquier pivote de la lista sirve. La semilla cambia el trabajo, no el resultado (prueba QS-06).
- *¿Por qué no `scala.util.Random`?* Tiene estado oculto y mutable, así que rompe la pureza.
- *¿Por qué no la recursión directa `Q(L) ++ E ++ Q(G)`?* Tiene dos llamadas que no son de cola, así que no puede llevar `@tailrec`. La lista de tareas hace todo en un bucle y la pila nunca crece (prueba QS-05: 100.000 elementos ya ordenados).
- *¿`@tailrec` hace que la memoria sea constante?* No: la lista de tareas y la salida siguen ocupando O(n).

---

## 3. Par de puntos más cercano (`ClosestPair`)

**Idea:** se ordena por x, se resuelve cada mitad y se revisa una franja alrededor de la línea divisoria para encontrar pares que la crucen.

**Pasos de `solve`** (todo con distancias **al cuadrado**; la raíz se saca una sola vez en `distance`):
1. Si hay 3 puntos o menos, se comparan todos los pares (`bruteForce`).
2. Se divide a la mitad por posición; `midX` es la x del primer punto de la derecha.
3. δ² = mínimo de las dos mitades.
4. La franja son los puntos con (x − midX)² < δ², tomados de **toda** la lista (`strip`).
5. La franja se ordena por y con Merge Sort (`sortByY`).
6. Cada punto se compara con a lo sumo 7 siguientes, y se para si Δy² ≥ el mínimo (`scanStrip`).

**Complejidad:** reordenar la franja en cada llamada da C(n) = 2C(n/2) + Θ(n log n) → **Θ(n log² n)**. Fue una **decisión consciente** de simplicidad. Devolver cada mitad ordenada por y y fusionarlas daría Θ(n log n).

**Lema de la franja (debes saber dibujarlo):** se toma un rectángulo de 2δ × δ que empieza en p y se divide en 8 cuadros de lado δ/2. Dos puntos de la misma mitad no caben en un mismo cuadro, porque estarían a ≤ δ/√2 < δ. Por eso hay como máximo 8 puntos y bastan 7 vecinos.

**Preguntas probables**
- *¿Por qué hace falta la franja?* Resolver las mitades no encuentra un par con un punto en cada lado.
- *¿Por qué no recorrer todos los pares de la franja?* La combinación sería Θ(n²) y se perdería la cota.
- *¿Por qué `Double` antes de restar?* Con `Int`, `Int.MaxValue − Int.MinValue` se desborda (prueba CP-06).
- *¿Por qué `<` y no `<=` en la franja?* Un par a distancia exactamente δ no mejora la respuesta, que ya es δ.
- *¿Para qué el límite de 7 si ya hay parada por Δy?* El límite de 7 es lo que **garantiza** que la revisión sea lineal; la parada por Δy solo acorta en la práctica.
- *¿Por qué no cumples el O(n log n) del enunciado?* Seguí el algoritmo que describe el enunciado (reordenar la franja), que da Θ(n log² n). Es una decisión de simplicidad documentada; la mejora consiste en fusionar las mitades ordenadas por y.
- *¿Por qué dos funciones de fusión (`mergeByX`, `mergeByY`)?* Para ordenar por x y por y sin pasar una función como parámetro, que sería de alto orden.

---

## 4. Pruebas (20, todas pasan)

- **Oráculos de fuerza bruta:** contar todos los pares, ordenamiento por inserción y comparar todos los pares de puntos.
- **Exhaustivas:** las 3.280 listas de longitud 0 a 7 sobre {−1, 0, 1} (incluyen vacía, un elemento, repetidos y negativos).
- **Entradas grandes:** 100.000 ordenados (QS-05), 100.000 iguales (QS-02, INV-06) y 10.001 puntos (CP-10).
- **Bordes:** extremos de `Int`, puntos duplicados, todos con la misma x y entradas inválidas.

*¿Las pruebas demuestran que el algoritmo es correcto?* No: lo demuestra la inducción (`doc/correctness.md`). Las pruebas dan confianza de que el código coincide con la demostración.

---

## 5. Experimento

- **Tamaños:** 16 y 64 (Toy), 1.024 y 8.192 (Pequeñas), 16.384 y 65.536 (Medianas), 1.048.576 (Grande).
- **Procedimiento:** 5 calentamientos sin medir y 11 ejecuciones medidas; se descarta la primera y se promedian 10.
- **Con n = 1.048.576:**
  - ClosestPair: 3.135 ms;
  - inversiones aleatorias: 1.058 ms;
  - QuickSort aleatorio: 981 ms;
  - inversiones decrecientes: 568 ms;
  - QuickSort con duplicados: 76 ms (casi 13 veces más rápido que el aleatorio).

**Cómo defender el análisis:**
- **R² no distingue los modelos.** Todos superan 0,998, porque el punto de un millón domina el ajuste. Por R², n log² n "gana" casi en todos, pero solo en la cuarta decimal.
- **La evidencia es el crecimiento.** Al multiplicar n por 16, el tiempo debería multiplicarse por 16 (n), por 20 (n log n) o por 25 (n log² n):
  - ClosestPair crece ×27,3, como n log² n;
  - QuickSort con duplicados crece ×18,8, cerca de lineal.
- **Desviaciones:** el recolector de basura (con un millón de elementos las listas ocupan cientos de MB), el JIT y los procesos del sistema.
- *¿Por qué descartar la primera ejecución?* Todavía incluye la compilación JIT y la carga de clases.
- *¿El ajuste demuestra la cota?* No: solo cubre tamaños finitos. La cota la demuestra el análisis de complejidad.

---

## 6. Inducción estructural en una frase

No se supone correcto el resultado de la llamada actual. Se supone correcto el de los subproblemas **estrictamente menores** (hipótesis inductiva) y se justifica cómo combinarlos. Siempre hay que mostrar el caso base, la hipótesis, el paso y la terminación.

## 7. Antes de sustentar, practica en vivo

- Ejecutar `sbt test` y `sbt "runMain ti1.Main"`.
- Hacer a mano la traza de `mergeAndCount` y la de `sortWithAcc` con `[3, 1, 2]`.
- Explicar qué pasaría si `QuickSort3` usara `head` como pivote con una lista ordenada.
- Dibujar la franja y los 8 cuadros de lado δ/2.
