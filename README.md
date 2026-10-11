# Integrative Assignment 1 — CyED II (2026-2)

Purely functional Scala solutions to three divide-and-conquer problems: counting inversions, 3-way randomized Quick Sort and closest pair of points.

**Author:** Juan Andrés Ariza López ([Ju4Cr0](https://github.com/Ju4Cr0))

## Run

Requires JDK 17, sbt and Python 3 (standard library only).

```bash
sbt test                      # unit tests
sbt "runMain ti1.Main"        # assignment examples
sbt "runMain ti1.Benchmark"   # timing experiment -> data/
python scripts/analyze.py     # plots and report -> doc/
```

To run on a file (one integer per line, or `x y` per line for points):

```bash
sbt "runMain ti1.Main inversions data/input-inversions-random-16.txt"
```

Use `quicksort` or `closest` in place of `inversions` for the other algorithms.

## Layout

| Path | Content |
|---|---|
| `src/main/scala/ti1/` | Algorithms, data generation, file I/O, benchmark |
| `src/test/scala/ti1/` | Tests |
| `doc/` | Test design, correctness proofs, complexity, experiment report, AI log |
| `data/` | Inputs and measurements |
| `scripts/analyze.py` | Builds the experiment report |

## Design

- Only recursion and pattern matching; no `map`, `filter` or other higher-order functions.
- Algorithms are pure. I/O and timing live in `InputFiles` and `Benchmark`.
- Inversion count is a `Long`, since n(n−1)/2 overflows `Int`.
- Quick Sort takes its random seed as a parameter.
