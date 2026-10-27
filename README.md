# Integrative Assignment 1 — CyED II (2026-2)

Purely functional Scala solutions to three divide-and-conquer problems: counting inversions, 3-way randomized Quick Sort and closest pair of points.

**Author:** Juan Andrés Ariza López ([Ju4Cr0](https://github.com/Ju4Cr0))

## Run

Requires JDK 17, sbt 1.10.7 and Python 3 (standard library only).

```bash
sbt test                            # JUnit tests
sbt "runMain ti1.Main"              # assignment examples
sbt "runMain ti1.utils.Benchmark"   # timing experiment -> data/
python scripts/analyze.py           # plots and report -> doc/
```

On Windows, if `sbt` is not on the PATH, use its full path: `& "C:\Program Files (x86)\sbt\bin\sbt.bat" test`.

To run one algorithm on a file:

```bash
sbt "runMain ti1.Main inversions data/input-inversions-random-16.txt"
```

| Algorithm | Argument | File format | Output |
|---|---|---|---|
| Counting inversions | `inversions` | one integer per line | number of inversions |
| 3-way Quick Sort | `quicksort` | one integer per line | sorted values, space-separated |
| Closest pair | `closest` | `x y` per line | minimum distance, 4 decimals |

## Layout

| Path | Content |
|---|---|
| `src/main/scala/ti1/algorithms/` | `CountingInversions`, `QuickSort3`, `ClosestPair` |
| `src/main/scala/ti1/utils/` | `DataGenerator`, `InputFiles`, `Benchmark` |
| `src/main/scala/ti1/Main.scala` | Command-line entry point |
| `src/test/scala/ti1/algorithms/` | One JUnit 4 test class per algorithm |
| `doc/` | Test design, correctness proofs, complexity, experiment report, AI log |
| `data/` | Inputs and measurements |
| `scripts/analyze.py` | Builds the experiment report |

## Design

- Only recursion and pattern matching; no `var`, `map`, `filter` or other higher-order functions.
- Each algorithm is a class with one public method: `count`, `sort` or `distance`. I/O and timing live in `utils`.
- Inversion count is a `Long`, since n(n−1)/2 overflows `Int`.
- `QuickSort3` takes its pseudo-random seed in the constructor, so the same seed always gives the same result.
- `ClosestPair` re-sorts the strip by y in every call, as the assignment describes. This keeps the code simple at the cost of Θ(n log² n) instead of Θ(n log n).
