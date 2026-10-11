# Preparación para sustentación

## Inversiones

Una inversión es un par de posiciones fuera de orden, no un intercambio ni una diferencia de valores. En `[2,3,9,2,9]` los pares son `(1,3)` y `(2,3)`. Si las cabezas de las mitades ordenadas cumplen `leftHead>rightHead`, la cabeza derecha es menor que **todos** los elementos izquierdos restantes: por eso sumamos remaining. Cuando son iguales, extraemos de la izquierda sin contar inversión. Long evita desbordamiento en listas grandes.

Debes poder modificar `merge` para mostrar manualmente sus acumuladores en un ejemplo y explicar por qué la división y fusión cuestan tiempo lineal en cada nivel.

## Quick Sort

Particionar con p produce menores, iguales y mayores. Solo ordenamos recursivamente los dos grupos extremos: los iguales ya están ubicados. Con todos los elementos iguales se hace una sola partición; de ahí Θ(n). Una semilla es estado explícito y reproducible: misma entrada y semilla, misma salida. El azar afecta el trabajo, no la correctitud.

El intérprete de tareas usa `Sort(less), Emit(equal), Sort(greater)` para representar la concatenación recursiva sin llamadas pendientes en la pila JVM. `@tailrec` no significa memoria total constante: las listas de tareas y la salida también ocupan memoria.

Debes poder señalar el peor caso Θ(n²) y explicar por qué el método maestro no resuelve `T(n)=T(n−1)+Θ(n)`.

## Puntos cercanos

Dividir por x resuelve pares dentro de las mitades, pero puede perder el óptimo que cruza la línea. La franja recupera esos candidatos. El orden y permite descartar vecinos verticalmente lejanos; el argumento de celdas limita la búsqueda a siete sucesores. Conservamos el orden y mediante fusión, sin ordenar nuevamente cada franja.

La comparación usa distancias cuadradas y toma raíz solo al final. Dos puntos distintos pueden compartir coordenadas: su distancia es cero. Para evitar desbordamiento de Int, primero convertimos a Double y después restamos.

Debes poder dibujar una división donde el óptimo cruza la línea y explicar por qué recorrer **todos** los pares de la franja destruiría la cota temporal.

## Inducción y experimentación

En una prueba por inducción no asumimos el resultado de la llamada actual: suponemos correctos los subproblemas estrictamente menores y justificamos cómo combinarlos. En la inducción estructural usamos el árbol de descomposición o la cabeza/cola de las listas. El lema de la franja es necesario: decir que las mitades son correctas no prueba el caso cruzado.

Las recurrencias de inversiones y puntos son `2T(n/2)+Θ(n)`: a=2, b=2, p=1, caso 2, Θ(n log n). El ajuste experimental es una función en ms sobre tamaños finitos; no demuestra una cota. Debes explicar calentamiento JIT, GC, variación del host, semilla fija y por qué la primera medición se descarta.

Antes de entregar, cada integrante debería ejecutar una prueba, introducir un cambio controlado y explicar su impacto. No se ha registrado todavía esa revisión del equipo.
