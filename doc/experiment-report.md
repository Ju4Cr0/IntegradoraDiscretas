# Experiment report

## Setup

Same machine for every run, JVM with `-Xms256m -Xmx2g`:

```
Recorded UTC: 2026-10-27T09:40:49.157276200Z
OS: Windows 11 10.0 amd64
Java: 17.0.20.1+1
Available logical processors: 16
CPU: AMD Ryzen 7 260 w/ Radeon 780M Graphics (AMD64 Family 25 Model 117 Stepping 2, AuthenticAMD)
Physical RAM (GiB): 31.3
Maximum JVM heap (MiB): 2048
Scala: 2.13.16
Additional warmups: 5 per dataset; recorded runs: 11; discarded: run 0; averaged: runs 1..10
Seed: 20261011; sizes: 16, 64, 1024, 8192, 16384, 65536, 1048576
```

- **Sizes:** 16, 64, 1,024, 8,192, 16,384, 65,536, 1,048,576. Categories follow the assignment: Toy < 10², Small < 10⁴, Medium < 10⁵, Large ≥ 10⁶.
- **Scenarios:** random and descending lists (inversions); random values and only 5 distinct values (Quick Sort); random points in a 10⁹ range (closest pair).
- **Procedure:** inputs come from a fixed seed and are saved to `data/input-*.txt`. Each input gets 5 untimed warm-up runs, then 10 + 1 timed runs; the first timed run is discarded and the other 10 are averaged. Raw times are in `data/raw.csv`.
- **Fitting:** least squares of `a·f(n) + b` (a, b ≥ 0) for f(n) = n, n log n, n log² n and n². Each plot shows the measurements, the best fit and the theoretical curve scaled to the last point.
- **Theory:** Θ(n log n) for inversions and random Quick Sort, Θ(n) for Quick Sort with 5 distinct values (O(n·k) with fixed k), and Θ(n log² n) for closest pair, which re-sorts the strip in every call (see `complexity.md`).

## Results

### Inversions — random

| Category | n | Mean (ms) | Std. dev. (ms) |
|---|---:|---:|---:|
| Toy | 16 | 0.156 | 0.061 |
| Toy | 64 | 0.532 | 0.493 |
| Small | 1,024 | 2.058 | 0.991 |
| Small | 8,192 | 7.759 | 1.183 |
| Medium | 16,384 | 9.396 | 2.163 |
| Medium | 65,536 | 48.253 | 12.271 |
| Large | 1,048,576 | 1057.878 | 101.729 |

![Inversions — random](figures/inversions-random.svg)

### Inversions — descending

| Category | n | Mean (ms) | Std. dev. (ms) |
|---|---:|---:|---:|
| Toy | 16 | 0.003 | 0.005 |
| Toy | 64 | 0.006 | 0.000 |
| Small | 1,024 | 0.237 | 0.006 |
| Small | 8,192 | 3.508 | 1.381 |
| Medium | 16,384 | 5.404 | 0.646 |
| Medium | 65,536 | 23.707 | 4.646 |
| Large | 1,048,576 | 568.150 | 57.502 |

![Inversions — descending](figures/inversions-descending.svg)

### 3-way Quick Sort — random

| Category | n | Mean (ms) | Std. dev. (ms) |
|---|---:|---:|---:|
| Toy | 16 | 0.092 | 0.031 |
| Toy | 64 | 0.237 | 0.074 |
| Small | 1,024 | 1.721 | 0.295 |
| Small | 8,192 | 5.833 | 0.603 |
| Medium | 16,384 | 8.144 | 1.401 |
| Medium | 65,536 | 35.902 | 6.788 |
| Large | 1,048,576 | 980.812 | 50.853 |

![3-way Quick Sort — random](figures/quicksort-random.svg)

### 3-way Quick Sort — duplicates

| Category | n | Mean (ms) | Std. dev. (ms) |
|---|---:|---:|---:|
| Toy | 16 | 0.001 | 0.000 |
| Toy | 64 | 0.002 | 0.000 |
| Small | 1,024 | 0.032 | 0.004 |
| Small | 8,192 | 0.346 | 0.034 |
| Medium | 16,384 | 0.711 | 0.126 |
| Medium | 65,536 | 4.023 | 1.819 |
| Large | 1,048,576 | 75.771 | 22.487 |

![3-way Quick Sort — duplicates](figures/quicksort-duplicates.svg)

### Closest pair — random

| Category | n | Mean (ms) | Std. dev. (ms) |
|---|---:|---:|---:|
| Toy | 16 | 0.108 | 0.010 |
| Toy | 64 | 0.377 | 0.085 |
| Small | 1,024 | 4.862 | 1.751 |
| Small | 8,192 | 10.639 | 1.335 |
| Medium | 16,384 | 19.808 | 1.699 |
| Medium | 65,536 | 114.770 | 15.336 |
| Large | 1,048,576 | 3135.221 | 138.804 |

![Closest pair — random](figures/closest-random.svg)

### Comparison

![Fitted curves](figures/comparison.svg)

"Growth" is how much the time multiplies from n = 65,536 to n = 1,048,576.

| Scenario | Theory | Best fit | R² n | R² n log n | R² n log² n | R² n² | Measured growth | Theoretical growth |
|---|---|---|---:|---:|---:|---:|---:|---:|
| Inversions — random | n log n | n log² n | 0.9996 | 1.0000 | 1.0000 | 0.9985 | ×21.9 | ×20.0 |
| Inversions — descending | n log n | n log² n | 0.9994 | 0.9999 | 1.0000 | 0.9987 | ×24.0 | ×20.0 |
| 3-way Quick Sort — random | n log n | n log² n | 0.9991 | 0.9998 | 1.0000 | 0.9991 | ×27.3 | ×20.0 |
| 3-way Quick Sort — duplicates | n | n log n | 0.9998 | 1.0000 | 0.9998 | 0.9978 | ×18.8 | ×16.0 |
| Closest pair — random | n log² n | n log² n | 0.9991 | 0.9998 | 1.0000 | 0.9991 | ×27.3 | ×25.0 |

## Analysis

- **Pattern:** time grows steadily with n in every scenario; nothing grows quadratically or exponentially.
- **R² does not separate the models:** the R² of n, n log n and n log² n differ only in the fourth decimal, because the largest size dominates the least-squares error and any increasing curve near that point scores well. The "best fit" column only shows the model with the slightly higher R², so it is not evidence on its own.
- **Growth factor is the better evidence:** multiplying n by 16 multiplies the time by about ×16 for n, ×20 for n log n and ×25 for n log² n. Closest pair grows like n log² n, as expected from re-sorting the strip in every call. Quick Sort with 5 distinct values stays close to linear. Inversions and random Quick Sort grow a little faster than n log n.
- **Does theory predict the time?** It predicts the shape of the growth, not the milliseconds. At n ≈ 10⁶ the measured growth is somewhat above the theoretical one, because immutable lists use hundreds of MB and the garbage collector works harder.
- **3-way partition:** with 5 distinct values Quick Sort is more than 10× faster than on random values at the largest size.
- **Unusual sizes:** Toy times are microseconds, so clock noise dominates. Between 8,192 and 16,384 several scenarios grow less than expected, which points to JIT optimisation between series.

## Sources of error

- Garbage collection pauses, which grow with memory use.
- JIT compilation may not be finished after 5 warm-up runs.
- Background processes of the operating system.
- One input per size (fixed seed).
- Quick Sort timing includes a linear pass over the output to consume the result.
