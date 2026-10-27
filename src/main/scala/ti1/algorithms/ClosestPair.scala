
package ti1.algorithms

import scala.annotation.tailrec

/**
 * Finds the minimum Euclidean distance between two points in a two-dimensional plane.
 *
 * Uses a divide-and-conquer algorithm: the points are sorted by x, each half is solved
 * recursively, and a strip around the dividing line is checked for pairs that cross it.
 * Distances are compared squared, so the square root is taken only once at the end.
 *
 * Time: Θ(n log² n). The strip is sorted by y in every call, so
 * T(n) = 2T(n/2) + Θ(n log n), which gives Θ(n log² n).
 * Space: O(n).
 *
 * All input points must contain exactly two integer coordinates.
 * At least two points are required.
 */
class ClosestPair {

  /**
   * Computes the minimum Euclidean distance between any two points.
   *
   * @param points a list of points represented as lists of two integers
   * @return the minimum Euclidean distance between two points
   * @throws IllegalArgumentException if fewer than two points are provided
   *                                  or a point does not contain exactly two coordinates
   */
  def distance(points: List[List[Int]]): Double = {

    // Validate that at least two points exist.
    require(
      points match {
        case _ :: _ :: _ => true
        case _ => false
      },
      "At least two points are required."
    )

    // Validate that every point has exactly two coordinates.
    require(
      allPairs(points),
      "Each point must contain exactly two coordinates."
    )

    val pairs = toPairs(points)
    val pointsByX = sortByX(pairs)
    val minSquared = solve(pointsByX, pointsByX.length)

    math.sqrt(minSquared)
  }

  /**
   * Converts validated coordinate lists into integer tuples.
   *
   * Uses tail recursion and an accumulator. The original order is not
   * preserved because the points are sorted before solving the problem.
   *
   * @param points a list of previously validated points
   * @return a list of coordinate tuples
   */
  private def toPairs(points: List[List[Int]]): List[(Int, Int)] = {

    @tailrec
    def loop(
              remaining: List[List[Int]],
              acc: List[(Int, Int)]
            ): List[(Int, Int)] = {

      remaining match {

        // No more points to convert.
        case Nil =>
          acc

          // Convert a valid point into a tuple.
        case (x :: y :: Nil) :: rest =>
          loop(rest, (x, y) :: acc)

          // Unreachable when distance validates the input first.
        case _ =>
          acc
      }
    }

    loop(points, Nil)
  }

  /**
   * Checks whether every point contains exactly two coordinates.
   *
   * @param points the points to validate
   * @return true if all points are valid, false otherwise
   */
  @tailrec
  private def allPairs(points: List[List[Int]]): Boolean = {

    points match {

      // An empty list contains no invalid points.
      case Nil =>
        true

        // Validate the remaining points.
      case (_ :: _ :: Nil) :: rest =>
        allPairs(rest)

        // Any other structure is invalid.
      case _ =>
        false
    }
  }

  /**
   * Sorts points in ascending order by their x-coordinate using merge sort.
   *
   * @param points the points to sort
   * @return the points sorted by x-coordinate
   */
  private def sortByX(
                       points: List[(Int, Int)]
                     ): List[(Int, Int)] = {

    points match {

      // An empty list is already sorted.
      case Nil =>
        Nil

        // A single point is already sorted.
      case _ :: Nil =>
        points

        // Divide, recursively sort, and merge.
      case _ =>
        val middle = points.length / 2
        val (left, right) = points.splitAt(middle)

        val sortedLeft = sortByX(left)
        val sortedRight = sortByX(right)

        mergeByX(sortedLeft, sortedRight)
    }
  }

  /**
   * Sorts points in ascending order by their y-coordinate using merge sort.
   *
   * @param points the points to sort
   * @return the points sorted by y-coordinate
   */
  private def sortByY(
                       points: List[(Int, Int)]
                     ): List[(Int, Int)] = {

    points match {

      // An empty list is already sorted.
      case Nil =>
        Nil

        // A single point is already sorted.
      case _ :: Nil =>
        points

        // Divide, recursively sort, and merge.
      case _ =>
        val middle = points.length / 2
        val (left, right) = points.splitAt(middle)

        val sortedLeft = sortByY(left)
        val sortedRight = sortByY(right)

        mergeByY(sortedLeft, sortedRight)
    }
  }

  /**
   * Merges two lists previously sorted by x-coordinate.
   *
   * Uses a tail-recursive accumulator and restores ascending order
   * when one of the input lists becomes empty.
   *
   * @param a the first sorted list
   * @param b the second sorted list
   * @return a merged list sorted by x-coordinate
   */
  private def mergeByX(a: List[(Int, Int)], b: List[(Int, Int)]): List[(Int, Int)] = {

    @tailrec
    def loop(left: List[(Int, Int)], right: List[(Int, Int)], acc: List[(Int, Int)]): List[(Int, Int)] = {

      (left, right) match {

        // The left list is empty.
        case (Nil, _) =>
          acc.reverse ::: right

          // The right list is empty.
        case (_, Nil) =>
          acc.reverse ::: left

          // Compare the x-coordinates.
        case (headA :: tailA, headB :: tailB) =>
          if (headA._1 <= headB._1) {
            loop(tailA, right, headA :: acc)
          } else {
            loop(left, tailB, headB :: acc)
          }
      }
    }

    loop(a, b, Nil)
  }

  /**
   * Merges two lists previously sorted by y-coordinate.
   *
   * @param a the first sorted list
   * @param b the second sorted list
   * @return a merged list sorted by y-coordinate
   */
  private def mergeByY(a: List[(Int, Int)], b: List[(Int, Int)]): List[(Int, Int)] = {

    @tailrec
    def loop(left: List[(Int, Int)], right: List[(Int, Int)], acc: List[(Int, Int)]): List[(Int, Int)] = {

      (left, right) match {

        // The left list is empty.
        case (Nil, _) =>
          acc.reverse ::: right

          // The right list is empty.
        case (_, Nil) =>
          acc.reverse ::: left

          // Compare the y-coordinates.
        case (headA :: tailA, headB :: tailB) =>
          if (headA._2 <= headB._2) {
            loop(tailA, right, headA :: acc)
          } else {
            loop(left, tailB, headB :: acc)
          }
      }
    }

    loop(a, b, Nil)
  }

  /**
   * Calculates the squared Euclidean distance between two points.
   *
   * Converts coordinates to Double before subtraction to prevent
   * integer overflow when working with extreme integer values.
   *
   * @param p the first point
   * @param q the second point
   * @return the squared Euclidean distance
   */
  private def squaredDistance(p: (Int, Int), q: (Int, Int)): Double = {

    val (x1, y1) = p
    val (x2, y2) = q

    val dx = x1.toDouble - x2.toDouble
    val dy = y1.toDouble - y2.toDouble

    dx * dx + dy * dy
  }

  /**
   * Finds the minimum squared distance by comparing every distinct pair.
   *
   * Intended for the divide-and-conquer base case containing at most
   * three points.
   *
   * @param points the points to compare
   * @return the minimum squared distance, or positive infinity
   *         if fewer than two points are provided
   */
  private def bruteForce(points: List[(Int, Int)]): Double = {

    @tailrec
    def compareWith(point: (Int, Int), remaining: List[(Int, Int)], minSquared: Double): Double = {

      remaining match {

        // No more points to compare.
        case Nil =>
          minSquared

          // Compare the current pair.
        case head :: tail =>
          val pairDistance = squaredDistance(point, head)
          val newMinSquared = math.min(minSquared, pairDistance)

          compareWith(point, tail, newMinSquared)
      }
    }

    @tailrec
    def loop(remaining: List[(Int, Int)], minSquared: Double): Double = {

      remaining match {

        // All pairs have been processed.
        case Nil =>
          minSquared

          // Compare the current point with subsequent points.
        case head :: tail =>
          val newMinSquared = compareWith(head, tail, minSquared)

          loop(tail, newMinSquared)
      }
    }

    loop(points, Double.PositiveInfinity)
  }

  /**
   * Selects points close to the vertical dividing line.
   *
   * A point belongs to the strip when its squared horizontal
   * distance from the dividing line is less than deltaSquared.
   *
   * @param points the points to examine
   * @param midX the x-coordinate of the dividing line
   * @param deltaSquared the current minimum squared distance
   * @return the points belonging to the strip
   */
  private def strip(points: List[(Int, Int)], midX: Int, deltaSquared: Double): List[(Int, Int)] = {

    @tailrec
    def loop(remaining: List[(Int, Int)], acc: List[(Int, Int)]): List[(Int, Int)] = {

      remaining match {

        // No more points to process.
        case Nil =>
          acc

          // Check whether the point belongs to the strip.
        case (x, y) :: rest =>
          val dx = x.toDouble - midX.toDouble

          if (dx * dx < deltaSquared) {
            loop(rest, (x, y) :: acc)
          } else {
            loop(rest, acc)
          }
      }
    }

    loop(points, Nil)
  }

  /**
   * Searches for a closer pair within a strip sorted by y-coordinate.
   *
   * Each point is compared with at most seven subsequent points.
   * Comparisons stop early when the squared vertical separation
   * cannot improve the current minimum.
   *
   * @param stripByY the strip points sorted by y-coordinate
   * @param deltaSquared the minimum squared distance found in both halves
   * @return the smallest squared distance found
   */
  private def scanStrip(stripByY: List[(Int, Int)], deltaSquared: Double): Double = {

    @tailrec
    def compareNeighbors(point: (Int, Int), remaining: List[(Int, Int)], neighborsLeft: Int, minSquared: Double): Double = {

      remaining match {

        // No more neighbors to compare.
        case Nil =>
          minSquared

          // Stop after checking at most seven neighbors.
        case _ if neighborsLeft == 0 =>
          minSquared

        case next :: rest =>
          val dy = next._2.toDouble - point._2.toDouble

          // Later points cannot improve the current minimum.
          if (dy * dy >= minSquared) {
            minSquared
          } else {
            val pairDistance = squaredDistance(point, next)
            val newMinSquared = math.min(minSquared, pairDistance)

            compareNeighbors(
              point,
              rest,
              neighborsLeft - 1,
              newMinSquared
            )
          }
      }
    }

    @tailrec
    def loop(remaining: List[(Int, Int)], minSquared: Double): Double = {

      remaining match {

        // All points have been processed.
        case Nil =>
          minSquared

          // Compare the current point with subsequent neighbors.
        case point :: rest =>
          val newMinSquared =
            compareNeighbors(point, rest, 7, minSquared)

          loop(rest, newMinSquared)
      }
    }

    loop(stripByY, deltaSquared)
  }

  /**
   * Recursively finds the minimum squared distance using divide and conquer.
   *
   * Splits the x-sorted points into two halves, solves each half,
   * and examines the strip around the dividing line for closer pairs.
   *
   * @param pointsByX the points sorted by x-coordinate
   * @param size the number of points in the current subproblem
   * @return the minimum squared distance
   */
  private def solve(pointsByX: List[(Int, Int)], size: Int): Double = {

    if (size <= 3) {

      // Solve small inputs using brute force.
      bruteForce(pointsByX)

    } else {

      // Divide the points into two halves.
      val middle = size / 2
      val (left, right) = pointsByX.splitAt(middle)

      right match {

        // The right half starts at the dividing line.
        case (midX, _) :: _ =>

          // Find the minimum squared distance in each half.
          val leftSquared = solve(left, middle)
          val rightSquared = solve(right, size - middle)

          val deltaSquared = math.min(leftSquared, rightSquared)

          // Build and sort the strip by y-coordinate.
          val stripPoints = strip(pointsByX, midX, deltaSquared)
          val stripByY = sortByY(stripPoints)

          // Find the minimum squared distance across the division.
          scanStrip(stripByY, deltaSquared)

          // Unreachable when size is greater than three.
        case Nil =>
          bruteForce(pointsByX)
      }
    }
  }
}
