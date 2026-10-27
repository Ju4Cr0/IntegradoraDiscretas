# Test design

Tests are JUnit 4 classes in `src/test/scala/ti1/algorithms/`, one per algorithm, and run with `sbt test`. Each `@Test` method is reported separately.

Results are checked against simple brute-force oracles: counting every pair for inversions, insertion sort for Quick Sort and comparing every pair of points for closest pair.

| ID | JUnit method | Goal | Input | Expected |
|---|---|---|---|---|
| INV-01 | `CountingInversionsTest.testStatementExample` | Assignment example | `[2, 3, 9, 2, 9]` | `2` |
| INV-02 | `CountingInversionsTest.testDescendingExample` | Decreasing list | `[9, 7, 5, 3]` | `6` |
| INV-03 | `CountingInversionsTest.testCountBeyondIntRange` | Count above `Int.MaxValue` | decreasing list of 100,000 | `4,999,950,000` |
| INV-04 | `CountingInversionsTest.testExhaustive` | Every small list (empty, single, repeated, negative) | all 3,280 lists of length 0–7 over `{-1, 0, 1}` | matches the oracle |
| INV-05 | `CountingInversionsTest.testLargeAscendingSequence` | Sorted input has no inversions | ascending list of 100,000 | `0` |
| INV-06 | `CountingInversionsTest.testLargeEqualSequence` | Equal values are never an inversion | 100,000 equal values | `0` |
| INV-07 | `CountingInversionsTest.testExtremeValues` | `Int` extremes | `[Int.MaxValue, Int.MinValue]` | `1` |
| QS-01 | `QuickSortTest.testRepeatedExample` | Repeated values | `[4, 4, 1, 9, 4, 1, 4, 4]` | `[1, 1, 4, 4, 4, 4, 4, 9]` |
| QS-02 | `QuickSortTest.testLargeEqualSequence` | No stack overflow | 100,000 equal values | same list |
| QS-03 | `QuickSortTest.testExhaustive` | Every small list | all 3,280 lists of length 0–7 over `{-1, 0, 1}` | matches the oracle |
| QS-04 | `QuickSortTest.testStatementExample` | Assignment example | `[2, 3, 9, 2, 2]` | `[2, 2, 2, 3, 9]` |
| QS-05 | `QuickSortTest.testLargeSortedSequence` | Already sorted input, the worst case for a `head` pivot | ascending list of 100,000 | same list, no stack overflow |
| QS-06 | `QuickSortTest.testDifferentSeedsSameResult` | The seed changes the work, not the result | `[9, 2, 5, 2, -1, 8, 0, 9]` with seeds 1 and 42 | `[-1, 0, 2, 2, 5, 8, 9, 9]` for both |
| QS-07 | `QuickSortTest.testExtremeValues` | `Int` extremes and repeated values | `[MAX, 0, MIN, MAX, -1, MIN]` | `[MIN, MIN, -1, 0, MAX, MAX]` |
| CP-01 | `ClosestPairTest.testTriangle` | Assignment example | `(0,0), (3,4)` | `5.0` |
| CP-02…06 | `ClosestPairTest.testReferenceCases` | Assignment example; pair crossing the middle line; same x for every point; duplicate points; `Int` extremes | `(0,0),(3,4),(1,1)`; `(-4,0),(-1,10),(0,10),(4,0)`; `(0,0),(0,10),(0,20),(0,21),(0,30)`; `(1,1),(2,2),(1,1),(4,4)`; `(MIN,MIN),(MAX,MAX)` | matches the oracle (`√2`, `1`, `1`, `0`, no overflow) |
| CP-07 | `ClosestPairTest.test300PointDatasets` | Random sets | 300 sets of 2–80 points | matches the oracle within 10⁻¹⁰ relative error |
| CP-08 | `ClosestPairTest.testFewerThanTwoPoints` | Invalid input | one point | `IllegalArgumentException` |
| CP-09 | `ClosestPairTest.testMalformedPoint` | Invalid input | a point with one coordinate | `IllegalArgumentException` |
| CP-10 | `ClosestPairTest.testLargeDatasetKnownDistance` | Deep recursion with a known answer | 10,000 points on the line y = 0 spaced 10 apart, plus `(1, 0)` | `1.0` |

Current result: **20 tests, 20 passed**.
