
package ti1.algorithms

import scala.annotation.tailrec

/** Randomized Quick Sort with a three-way partition (less, equal, greater than the pivot).
  *
  * Pivots are chosen with a linear congruential generator whose state is passed explicitly,
  * so the class is pure: the same seed always produces the same pivots and the same result.
  *
  * @param seed initial state of the pseudo-random generator
  */
class QuickSort3(seed: Long) {

  /** Sorts by processing a list of pending tasks in a single tail-recursive loop,
    * so the JVM stack never grows, even in the worst case.
    *
    * Tasks are taken greatest group first and their elements are prepended to `acc`,
    * which therefore stays in ascending order and never needs to be reversed.
    *
    * @param pending tasks still to process; `(values, true)` is a group of equal values that goes
    *                straight to the output, `(values, false)` is a group that still has to be sorted
    * @param acc     elements already placed, in ascending order; every pending element is smaller
    * @param state   current state of the pseudo-random generator
    * @return the pending elements in ascending order, followed by `acc`
    */
  @tailrec
  private def sortWithAcc(pending: List[(List[Int], Boolean)], acc: List[Int], state: Long): List[Int] = {
    pending match {

      case Nil =>
        (acc)


      case (values, ready) :: rest =>

        if (ready) {


          sortWithAcc(rest, values ::: acc, state)

        } else {

          values match {


            case Nil =>
              sortWithAcc(rest, acc, state)

            case x :: Nil =>
              sortWithAcc(rest, x :: acc, state)


            case _ =>


              // LCG step; floorMod keeps the index valid even if the Long overflows to a negative value
              val nextState = state * 1664525L + 1013904223L

              val pivotIndex =
                Math.floorMod(nextState, values.length.toLong).toInt


              val pivot = values(pivotIndex)


              val (less, equal, greater) =
                partition3(values, pivot)

              // Greater first: prepending each group to acc keeps acc ascending
              val newPending = (greater, false) :: (equal, true) :: (less, false) :: rest
              sortWithAcc(newPending, acc, nextState)
          }
        }
    }
  }

  /** Splits `values` in a single pass into the elements less than, equal to and greater than `pivot`.
    * Each element goes to exactly one group. The order inside each group is not preserved,
    * because `less` and `greater` are sorted afterwards and `equal` holds identical values.
    *
    * @param values list to split
    * @param pivot  value to compare against
    * @return `(less, equal, greater)`
    */
  private def partition3(values: List[Int], pivot: Int): (List[Int], List[Int], List[Int]) = {

    @tailrec
    def loop(remaining: List[Int], less: List[Int], equal: List[Int], greater: List[Int]): (List[Int], List[Int], List[Int]) = {

      remaining match {


        case Nil =>
          (less, equal, greater)
        case x :: xs =>
          if (x < pivot) {
            loop(xs, x :: less, equal, greater)

          } else if (x == pivot) {
            loop(xs, less, x :: equal, greater)
          } else {
            loop(xs, less, equal, x :: greater)
          }
      }
    }

    loop(values, Nil, Nil, Nil)
  }

  /** Sorts a list of integers in non-decreasing order.
    *
    * Time: expected Θ(n log n); Θ(n) when all values are equal and O(n·k) with k distinct values,
    * thanks to the three-way partition; worst case Θ(n²) if every pivot is a minimum or maximum.
    * Space: O(n) for the pending tasks and the output.
    *
    * @param values list to sort; may be empty and may contain repeated or negative values
    * @return a new list with the same elements in non-decreasing order
    */
  def sort(values: List[Int]): List[Int] = {
    sortWithAcc(List((values, false)), Nil, seed)
  }
}
