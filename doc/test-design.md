# Test design

Tests live in `src/test/scala/ti1/AlgorithmTests.scala` and run with `sbt test`. Any failed `assert` stops the run and names the case.

Results are checked against simple brute-force oracles: counting every pair for inversions, insertion sort for Quick Sort and comparing every pair of points for closest pair.

| ID | Goal | Input | Expected |
|---|---|---|---|
| INV-01 | Assignment example | `[2, 3, 9, 2, 9]` | `2` |
| INV-02 | Decreasing list | `[9, 7, 5, 3]` | `6` |
| INV-03 | Count above `Int.MaxValue` | decreasing list of 100,000 | `4,999,950,000` |
| QS-01 | Repeated values | `[4, 4, 1, 9, 4, 1, 4, 4]` | `[1, 1, 4, 4, 4, 4, 4, 9]` |
| QS-02 | No stack overflow | 100,000 equal values | same list |
| GEN-01 | Every small list (empty, single, repeated, negative) | all 3,280 lists of length 0–7 over `{-1, 0, 1}` | matches oracles |
| CP-01 | Assignment example | `(0,0), (3,4)` | `5.0` |
| CP-02 | Assignment example | `(0,0), (3,4), (1,1)` | `√2 ≈ 1.4142` |
| CP-03 | Closest pair crosses the middle line | `(-4,0), (-1,10), (0,10), (4,0)` | `1` |
| CP-04 | Same x for every point | `(0,0), (0,10), (0,20), (0,21), (0,30)` | `1` |
| CP-05 | Duplicate points | `(1,1), (2,2), (1,1), (4,4)` | `0` |
| CP-06 | `Int` extremes | `(MIN, MIN), (MAX, MAX)` | matches oracle, no overflow |
| CP-07 | Random sets | 300 sets of 2–80 points | matches oracle within 10⁻¹⁰ relative error |
| CP-08 | Invalid input | one point; a point with one coordinate | `IllegalArgumentException` |
