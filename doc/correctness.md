# Recursive definitions and correctness

Each proof uses structural induction: base case, inductive hypothesis (IH) and inductive step.

## 1. Counting inversions

An inversion is a pair of positions i < j with A[i] > A[j]. Equal values are not an inversion.

**Definition** (`CountingInversions.sortAndCount`, `CountingInversions.mergeAndCount`)

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
- *Step:* if b ≤ c, b is the smallest remaining element and is not greater than any element of C, so it adds no pairs; equal values are taken from the left without counting. If b > c, every element left in B is ≥ b > c, so c forms exactly |B| pairs. Either way the IH handles the rest.

**Theorem.** `MS(A)` returns A sorted and its inversion count.

- *Base:* [] and [a] are sorted with 0 inversions.
- *IH:* MS is correct on L and R.
- *Step:* every pair of positions lies either inside L (counted in iL), inside R (counted in iR), or across both (counted in c by the lemma; sorting each half does not change how many cross pairs exist). The groups do not overlap, so the total is iL + iR + c.

**Termination:** each split makes strictly smaller lists.

**Note on the code:** `mergeAndCount` keeps |B| in the parameter `leftRemaining`, computed once and decreased by 1 each time an element is taken from the left. This gives |b::B'| in O(1) per step, so the merge stays linear.

## 2. 3-way Quick Sort

**Definition** (`QuickSort3.partition3`, `QuickSort3.sort`)

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

**Correspondence with the code.** `sortWithAcc(pending, acc, state)` computes Q without nested calls. `pending` is a list of groups P₁, P₂, …, Pₖ, where `(X, true)` is a group of equal values and `(X, false)` a group still to sort. It keeps this invariant:

- every element of Pᵢ is ≥ every element of Pᵢ₊₁ (groups are ordered from greatest to smallest);
- every pending element is ≤ every element of `acc`, and `acc` is sorted.

Under the invariant, the result is Q(Pₖ) ++ … ++ Q(P₁) ++ `acc`.

- *Base:* `pending = []` returns `acc`, which is sorted and holds every element.
- *Step:* an empty group is dropped; a single element or a group of equal values is prepended to `acc`, which stays sorted because it is ≤ everything there; a group X with two or more values is replaced by `(G, false), (E, true), (L, false)` in that order. By the partition lemma G > E > L, so the invariant still holds, and Q(L) ++ E ++ Q(G) = Q(X), so the result does not change.
- *Termination:* each step removes a group or splits a group of size ≥ 2 into parts that are strictly smaller (E is non-empty).

Taking the greatest group first is what lets the code prepend to `acc` in O(1) and never reverse a list. The pseudo-random state only chooses p ∈ A; any p ∈ A satisfies the theorem, so the seed affects the work, never the result.

## 3. Closest pair of points

Distances are compared squared; the square root is taken once at the end. Input validation (`allPairs`, `require`) and conversion to tuples (`toPairs`) run before the recursion.

**Definition** (`ClosestPair.solve`). Points are first sorted by x with Merge Sort (`sortByX`). C(P) returns δ², the smallest squared distance in P.

- If |P| ≤ 3: compare all pairs (`bruteForce`).
- Otherwise:
  1. Split P by position into halves L and R; the middle line is the x of the first point of R.
  2. δ² = min(C(L), C(R)).
  3. Strip: points of P with (x − middle)² < δ² (`strip`).
  4. Sort the strip by y with Merge Sort (`sortByY`).
  5. Compare each strip point with the next ones (at most 7), stopping once the y-gap squared is ≥ the current minimum (`scanStrip`).
  6. Return the smallest value found, starting from δ².

**Merge Sort lemma.** `sortByX` and `sortByY` return their input sorted by x and by y. The proof is the theorem of section 1 without the counter: merging two sorted lists gives a sorted list with the same elements.

**Strip lemma.** If p ∈ L and q ∈ R are closer than δ, both are in the strip and at most 7 positions apart in y-order.

Both are less than δ from the middle line, so both are in the strip. Take the 2δ × δ rectangle starting at p and split it into 8 squares of side δ/2. Two points of the same half cannot share a square, since their distance would be at most δ/√2 < δ, contradicting δ. So the rectangle holds at most 8 points, p included.

**Theorem.** C(P) returns the minimum squared distance of P.

- *Base:* with 2 or 3 points all pairs are compared.
- *IH:* C(L) and C(R) return the minimum squared distance of L and R.
- *Step:* the closest pair is either inside L (dL²), inside R (dR²), or crosses the line. If a crossing pair beats δ, the strip lemma guarantees that it is in the strip and that the scan of the y-sorted strip reaches it. A pair at distance exactly δ does not need to be found, because δ is already the answer.

**Termination:** each split makes strictly smaller sets.

Coordinates are converted to `Double` before subtracting, so `Int` extremes cannot overflow.
