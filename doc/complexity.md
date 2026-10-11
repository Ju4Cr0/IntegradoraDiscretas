# Complexity

`n` is the list length.

## 1. Counting inversions

Splitting and merging are both Θ(n).

```
T(1) = Θ(1)
T(n) = 2T(n/2) + Θ(n)
```

Master theorem: a = 2, b = 2, n^(log_b a) = n = Θ(f(n)) → case 2 → **T(n) = Θ(n log n)**, for any input order.

Space: O(n) for the lists plus O(log n) stack frames.

## 2. 3-way Quick Sort

Choosing the pivot and partitioning into less / equal / greater is Θ(n). With group sizes l, e, g (e ≥ 1):

```
T(n) = T(l) + T(g) + Θ(n)
```

| Case | Recurrence | Result |
|---|---|---|
| Balanced pivot | T(n) = 2T(n/2) + Θ(n) | Θ(n log n) |
| Worst (pivot always min or max) | T(n) = T(n−1) + Θ(n) | Θ(n²) |
| All elements equal | one partition | Θ(n) |
| k distinct values | depth ≤ k, each level O(n) | O(nk), Θ(n) for fixed k |
| Expected (random pivot) | E[T(n)] = (2/n) Σ E[T(j)] + Θ(n) | Θ(n log n) |

Worst case expands to Θ(n) + Θ(n−1) + … + Θ(1) = Θ(n²); the master theorem does not apply because the size shrinks by 1, not by a factor. The expected case follows Cormen, ch. 7.

The 3-way partition is what makes repeated values cheap: elements equal to the pivot are never processed again.

Space: O(n). Recursion is replaced by a `@tailrec` loop over a list of pending tasks, so the JVM stack never overflows.

## 3. Closest pair of points

The initial sort by x is Θ(n log n). Each recursive call then does Θ(n) work: split, merge the y-sorted halves, build the strip and compare each strip point with at most 7 neighbours.

```
C(n) = 2C(n/2) + Θ(n)  →  Θ(n log n)
```

Total: **Θ(n log n)**.

The assignment suggests re-sorting the strip by y in every call, which gives C(n) = 2C(n/2) + Θ(n log n) = Θ(n log² n). Instead, each call returns its points sorted by y and the parent merges them in linear time, as in Merge Sort.

Space: O(n) plus O(log n) stack frames.

## Summary

| Algorithm | Time | Space |
|---|---|---|
| Inversions | Θ(n log n) | O(n) |
| 3-way Quick Sort | expected Θ(n log n), worst Θ(n²), all equal Θ(n) | O(n) |
| Closest pair | Θ(n log n) | O(n) |
