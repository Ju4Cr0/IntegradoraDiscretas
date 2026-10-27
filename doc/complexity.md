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

Space: O(n). Instead of two recursive calls, `sortWithAcc` is a single `@tailrec` loop over a list of pending groups, so the JVM stack never grows, even in the worst case. Groups are processed greatest first and prepended to the output, so no list is ever reversed.

## 3. Closest pair of points

Validation, conversion and the initial sort by x (Merge Sort) cost Θ(n) + Θ(n log n).

Each recursive call then splits the points (Θ(n)), builds the strip (Θ(n)), **sorts the strip by y with Merge Sort (Θ(n log n) in the worst case, when every point is in the strip)** and compares each strip point with at most 7 neighbours (Θ(n)). Groups of at most 3 points are solved by brute force in Θ(1).

```
C(n) = 2C(n/2) + Θ(n log n)
```

The master theorem with f(n) = Θ(n log n) = Θ(n^(log_b a) · log n) gives case 2 with k = 1:

**C(n) = Θ(n log² n)**

The same result by levels: level i has 2^i subproblems of size n/2^i, each costing Θ((n/2^i) log(n/2^i)), so level i costs Θ(n (log n − i)). Adding the log n levels gives Θ(n · (log n + (log n − 1) + … + 1)) = Θ(n log² n).

Total: Θ(n log n) + Θ(n log² n) = **Θ(n log² n)**.

This follows the algorithm described in the assignment, which re-sorts the strip in every call. Returning each half already sorted by y and merging the halves in linear time would lower the cost to Θ(n log n); that version was not chosen, to keep the recursion simple.

Space: O(n), plus O(log n) stack frames because the split is always by position at the middle.

## Summary

| Algorithm | Time | Space |
|---|---|---|
| Inversions | Θ(n log n) | O(n) |
| 3-way Quick Sort | expected Θ(n log n), worst Θ(n²), all equal Θ(n) | O(n) |
| Closest pair | Θ(n log² n) | O(n) |
