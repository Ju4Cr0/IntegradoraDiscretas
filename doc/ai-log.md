# Generative AI usage log

**AI use level: 3. Collaboration with GenAI.**
Model used: Claude (Claude Code). Assigned role: *personal tutor / functional programming mentor and technical documentation reviewer*.
Base rule imposed on the model: **"NO quiero que hagas el proyecto completo por mí"** — the author decides and writes the logic; Claude clarifies specific doubts, proposes options and reviews.

> Prompts are quoted exactly as typed (in Spanish). Every exchange follows the pattern: **specific question from the author → clarification from Claude → final decision by the author.**

---

## Reference version (before the refactor)

- **What it is:** a first complete version (`Algorithms.scala` with `object`s, `AlgorithmTests.scala`, docs and a first experiment), generated with AI.
- **How it was used:** only as a guide to the expected behavior. It was deleted after the refactor; the final code was written again under the rules below.

---

## Refactor with AI as tutor (Claude Code)

**Own input:** the project, the refactor plan (one class per algorithm, JUnit tests) and the rules: no `var`, no higher-order functions, recursion, `@tailrec`, pattern matching, immutable lists.

**Role given to the AI:**

> "Actúa como mi profesor particular, mentor de programación funcional y revisor de documentación técnica… NO quiero que hagas el proyecto completo por mí".

### Interaction log (specific questions → clarification → decision)

| # | Author's question (prompt) | Claude's clarification | Author's decision |
|---|---|---|---|
| 1 | "pivote con semilla ya que usar head no tendría sentido porque básicamente estaría forzando siempre el peor caso" | Explains why `head` degenerates to O(n²) on sorted input and how a fixed seed makes the pivot random but reproducible for the tests. | **Random pivot with a seed**, not `head`. |
| 2 | "la pila vamos con un acumulador de cola para usar el `@tailrec`, y evitar un desbordamiento" | Confirms that Quick Sort with partitioning + plain recursion is not *tail-recursive* and proposes rewriting it with a tail accumulator so `@tailrec` can be annotated. | **Tail accumulator** in `sortWithAcc` to enable `@tailrec`. |
| 3 | "evitemos el `reverse`, no tiene sentido agregarle O(n) para ordenarlo si podemos cambiar el orden" | Points out where the `reverse` appeared and how to invert the concatenation order to avoid it. | **No `reverse`** in Quick Sort. |
| 4 | "una tupla" | Weighs the pros and cons of representing points as `(Double, Double)` versus a case class. | **Points as tuples**. |
| 5 | "necesito validar para los test y benchmarks" | Suggests using `require` in constructors/inputs to fail fast and feed valid inputs to tests and benchmarks. | **Validation with `require`**. |

### Actual division of writing

- **Written by the author:** all the algorithm logic —
  `QuickSort3` (`sortWithAcc`, `partition3`, `sort`), `ClosestPair` (validation, conversion, both Merge Sorts, `bruteForce`, `strip`, `scanStrip`, `solve`, and most of its Scaladoc), `CountingInversions` (`sortAndCount`, `mergeAndCount`) and the test fixes (imports, `QuickSort3` seed).
-Docs, experiment report and slides updated to the new code
- **Written by the AI on request** (the author asked, Claude wrote):
  - Scaladoc of `QuickSort3` → *"el scaladoc te lo dejo a ti"*.
  - Complexity in the Scaladoc of `ClosestPair` and `CountingInversions`, the `count` in one line and renaming the class back to `CountingInversions` → *"hazlos tú"*, *"haz el 1 también"*.
  - `utils` updated to the new packages and classes; `var sink` in `Benchmark` replaced by a checksum → *"los utils TÚ ENCÁRGATE DE ESOS… si no influye en nada quítalo"*.
  - `Main` rewritten for the new classes → *"arregla main tú"*.
- **Checked by the AI on every review:** compilation and quick runs of each class on a scratch copy outside the project.

---

## Validation

- `sbt test`: 20 tests, 20 passed (12 kept from the reference version, 8 added by the author).
- `Main` prints `2`, `2 2 2 3 9` and `1.4142`.
- Benchmark repeated with the refactored code; see `experiment-report.md`.

---

### How the collaborative flow looks (summary)

1. **The author asks something concrete** (not "do the project for me").
2. **Claude clarifies the doubt and lays out options**, without writing the solution unless asked.
3. **The author decides and writes the logic.**
4. **Claude writes the supporting material** (Scaladoc, docs, `utils`, `Main`) when the author delegates it.
5. **Claude verifies** compilation and a quick run on every iteration.