# Generative AI log

AI use level: **3. Collaboration with GenAI**. Prompts are quoted as typed (in Spanish).

## Session 1 — Setup and first algorithms

- **Tool:** ChatGPT (GPT-4o)
- **Own input:** assignment statement (`CyEDII_2026-2_TI1.docx`) and a first draft of `Inversions.scala`.
- **Prompts:**
  1. "tengo este merge sort en scala, como lo modifico para contar inversiones sin dejar de ser funcional? no puedo usar map ni filter"
  2. "y como evito que sea recursivo normal, necesito que sea tail recursive con acumulador"
- **Output:** suggestion to carry an accumulator and count the remaining left half when taking from the right half.
- **Own review:** rewrote it with an explicit accumulator and `@tailrec`; the AI's first version still used `length`, so I replaced it with a counter to keep it linear.

## Session 2 — Three-way Quick Sort

- **Tool:** ChatGPT (GPT-4o)
- **Own input:** `QuickSort.scala` skeleton.
- **Prompts:**
  1. "particion de tres vias en scala puro, sin filter, con pattern matching"
  2. "por que el peor caso sigue siendo n^2 y como lo explico"
- **Output:** partition into `<`, `==`, `>` with three recursive helper functions; recurrence notes.
- **Own review:** kept the helpers tail-recursive; wrote the recurrence and its resolution myself in `complexity.md`.

## Session 3 — Closest Points

- **Tool:** ChatGPT (GPT-4o)
- **Own input:** `ClosestPoints.scala` and the strip idea from Cormen.
- **Prompts:**
  1. "closest pair en scala, ordenar por x y luego la franja por y, todo recursivo"
  2. "el enunciado dice mostrar 4 decimales, como formateo un Double en scala sin printf?"
- **Output:** divide-and-conquer skeleton; formatting tip for the final distance.
- **Own review:** limited the strip scan to at most 7 neighbours and wrote the induction proof by hand.

## Session 4 — Docs, translation and redundancy

- **Tool:** Claude (Claude Code)
- **Own input:** the full project (code, tests, docs).
- **Prompts:**
  1. "revisa los .md, quitá redundancia y traducí todo a inglés"
  2. "¿qué más corregirías en la redacción?"
  3. "completá los integrantes, solo yo"
  4. "keep it simple, no seas redundante en nada"
- **Output:** tightened README, translated docs to English, removed repeated definitions across `correctness.md`, `complexity.md` and `experiment-report.md`.
- **Own review:** checked every translated section against the original; fixed two places where the AI over-simplified a recurrence.

## Session 5 — Experiments review

- **Tool:** Claude (Claude Code)
- **Own input:** `Benchmark.scala`, `AlgorithmTests.scala` and the first batch of measurements.
- **Prompts:**
  1. "mis tamaños de entrada no llegan a 10^6, están mal las categorías"
  2. "las mediciones las tomé en otra máquina, como lo arreglo"
  3. "hacé que el script de gráficas use solo la librería estándar y grafique en SVG"
- **Output:** new size scale (16 → 1,048,576), CPU/RAM logging, pure-standard-library plotting.
- **Own review:** re-ran all measurements on my machine (Ryzen 7 260, 31.3 GiB, Windows 11) and re-generated the plots myself.

## Issues found during review

- The first experiment had no input of size ≥ 10⁶ and the size categories were mislabelled.
- Early measurements had been taken on a different machine.
- `build.sbt` did not set the JVM memory the report claimed.
- The test design listed a case that actually ran in `Benchmark`.

## Changes made

- Rewrote and simplified every doc in English.
- `Benchmark.scala`: new sizes, assignment categories, CPU/RAM logging.
- `build.sbt`: `-Xms256m -Xmx2g`.
- Plotting script: standard library only, SVG output, measured vs. theoretical table.
- Re-measured on my own machine.

## Validation

- Project compiles with no warnings.
- All tests pass.
- `Main` prints the expected `2`, `2 2 2 3 9` and `1.4142`.

## Own contributions

- Wrote the three algorithms and kept them pure, immutable and `@tailrec`.
- Designed the test cases and the test-design tables.
- Wrote the structural induction proofs and the recurrence solutions.
- Ran the experiments and produced the final plots and analysis.
- Decided the repository structure and the commit order.