# Recursive definitions and correctness

Each proof uses structural induction: base case, inductive hypothesis (IH) and inductive step.

## 1. Counting inversions

An inversion is a pair of positions i < j with A[i] > A[j].

**Definition** (`Inversions.sortAndCount`, `Inversions.merge`)

```
MS([])  = ([], 0)
MS([a]) = ([a], 0)
MS(L ++ R) = (S, iL + iR + c)   where (B, iL) = MS(L), (C, iR) = MS(R), (S, c) = Merge(B, C)

Merge([], C) = (C, 0)
Merge(B, []) = (B, 0)
Merge(b::B', c::C') = (b :: S, k)          if b ≤ c, with (S, k) = Merge(B', c::C')
                    = (c :: S, k + |b::B'|) if b > c, with (S, k) = Merge(b::B', C')
```

**Lemma.** For sorted B and C, `Merge(B, C)` returns them merged in order and the number of pairs b ∈ B, c ∈ C with b > c.

- *Base:* if either list is empty there are no cross pairs and the other list is already sorted.
- *IH:* Merge is correct on the lists left after removing one head.
- *Step:* if b ≤ c, b is the smallest remaining element and is not greater than any element of C, so it adds no pairs. If b > c, every element left in B is ≥ b > c, so c forms exactly |B| pairs. Either way the IH handles the rest.

**Theorem.** `MS(A)` returns A sorted and its inversion count.

- *Base:* [] and [a] are sorted with 0 inversions.
- *IH:* MS is correct on L and R.
- *Step:* every pair of positions lies either inside L (counted in iL), inside R (counted in iR), or across both (counted in c by the lemma; sorting each half does not change how many cross pairs exist). The groups do not overlap, so the total is iL + iR + c.

**Termination:** each split makes strictly smaller lists.

## 2. 3-way Quick Sort

**Definition** (`QuickSort.partition3`, `QuickSort.sort`)

```
Q([])  = []
Q([a]) = [a]
Q(A)   = Q(L) ++ E ++ Q(G)     with pivot p ∈ A and (L, E, G) = P(A, p)

P([], p)    = ([], [], [])
P(a::A', p) = a added to L, E or G of P(A', p), depending on a < p, a = p or a > p
```

**Lemma.** P(A, p) puts each element of A exactly once into the group that matches its comparison with p.

- *Base:* P([], p) gives three empty groups.
- *IH:* the lemma holds for A'.
- *Step:* the head a satisfies exactly one of <, =, > and goes only to that group.

**Theorem.** Q(A) is A sorted.

- *Base:* [] and [a] are sorted.
- *IH:* Q(L) and Q(G) are L and G sorted.
- *Step:* by the lemma, L < p, E = p and G > p, so Q(L) ++ E ++ Q(G) is sorted and has the same elements as A.

**Termination:** p ∈ A, so E is non-empty and L, G are strictly smaller than A.

**Note on the code:** `sort` avoids deep JVM recursion with a `@tailrec` loop over pending tasks. Expanding `Sort(A)` into `Sort(L), Emit(E), Sort(G)` is exactly the equation Q(A) = Q(L) ++ E ++ Q(G), so the result is the same.

## 3. Closest pair of points

Distances are compared squared; the square root is taken once at the end.

**Definition** (`ClosestPoints.solve`). Points are first sorted by x. C(P) returns (d², Y): the smallest squared distance and the points sorted by y.

- If |P| ≤ 3: compare all pairs.
- Otherwise:
  1. Split P into halves L and R; the middle line is the x of the first point of R.
  2. (dL², YL) = C(L), (dR², YR) = C(R), δ² = min(dL², dR²).
  3. Y = merge(YL, YR) by y.
  4. Strip: points of Y with (x − middle)² ≤ δ².
  5. Compare each strip point with the next ones (at most 7), stopping once the y-gap is ≥ δ.
  6. Return the smallest value found and Y.

**Strip lemma.** If p ∈ L and q ∈ R are closer than δ, both are in the strip and at most 7 positions apart in y-order.

Both are within δ of the middle line, so both are in the strip. Take the 2δ × δ rectangle starting at p and split it into 8 squares of side δ/2. Two points of the same half cannot share a square, since their distance would be at most δ/√2 < δ, contradicting δ. So the rectangle holds at most 8 points, p included.

**Theorem.** C(P) returns the minimum distance of P.

- *Base:* with 2 or 3 points all pairs are compared.
- *IH:* C(L) and C(R) return the correct minimum and y-order.
- *Step:* the closest pair is either inside L (dL), inside R (dR), or crosses the line. If a crossing pair beats δ, the lemma guarantees the strip scan finds it. Merging keeps all points in y-order for the parent.

**Termination:** each split makes strictly smaller sets.

Coordinates are converted to `Double` before subtracting, so `Int` extremes cannot overflow.
