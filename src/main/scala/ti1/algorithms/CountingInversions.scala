package ti1.algorithms
import scala.annotation.tailrec

/**
 * Counts the inversions of a list: pairs of positions i < j with A(i) > A(j).
 * Equal values are not an inversion.
 *
 * Uses Merge Sort modified so that merging two sorted halves also counts the pairs
 * that cross between them.
 *
 * Time: Θ(n log n) for any input order, since T(n) = 2T(n/2) + Θ(n).
 * Space: O(n).
 */
class CountingInversions {

  /**
   * Counts the number of inversions in a list.
   *
   * @param values the input integers; may be empty and may contain repeated or negative values
   * @return the total number of inversions, as a Long because it can reach n(n − 1)/2
   */
  def count(values: List[Int]): Long = sortAndCount(values)._2

  /**
   * Sorts the list and counts its inversions recursively.
   *
   * @param values the integers to process
   * @return the sorted list and its inversion count
   */
  private def sortAndCount(values: List[Int]): (List[Int], Long) = {

    values match {

      // An empty list has no inversions.
      case Nil =>
        (Nil, 0L)

        // A single element has no inversions.
      case _ :: Nil =>
        (values, 0L)

        // Divide, recursively count, and merge.
      case _ =>
        val middle = values.length / 2
        val (left, right) = values.splitAt(middle)

        val (sortedLeft, leftCount) = sortAndCount(left)
        val (sortedRight, rightCount) = sortAndCount(right)

        val (merged, crossCount) =
          mergeAndCount(sortedLeft, sortedRight)

        (merged, leftCount + rightCount + crossCount)
    }
  }

  /**
   * Merges two sorted lists and counts cross inversions.
   *
   * @param left the sorted left half
   * @param right the sorted right half
   * @return the merged sorted list and the number of cross inversions
   */
  private def mergeAndCount(left: List[Int], right: List[Int]): (List[Int], Long) = {

    @tailrec
    def loop(l: List[Int], r: List[Int], acc: List[Int], leftRemaining: Int, inversions: Long): (List[Int], Long) = {

      (l, r) match {

        // The left list is empty.
        case (Nil, _) =>
          (acc.reverse ::: r, inversions)

          // The right list is empty.
        case (_, Nil) =>
          (acc.reverse ::: l, inversions)

          // Compare the first elements of both lists.
        case (headL :: tailL, headR :: tailR) =>

          if (headL <= headR) {

            // No new inversion is found.
            loop(
              tailL,
              r,
              headL :: acc,
              leftRemaining - 1,
              inversions
            )

          } else {

            // Every remaining left element is greater than headR.
            loop(
              l,
              tailR,
              headR :: acc,
              leftRemaining,
              inversions + leftRemaining.toLong
            )
          }
      }
    }

    loop(left, right, Nil, left.length, 0L)
  }
}