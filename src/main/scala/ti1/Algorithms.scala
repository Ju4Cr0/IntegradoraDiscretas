package ti1

import scala.annotation.tailrec

/** Pure list utilities, without higher-order functions. */
object ListTools {
  /** @param values input list
    * @return reversed input
    */
  def reverse[A](values: List[A]): List[A] = {
    @tailrec def loop(rest: List[A], result: List[A]): List[A] = rest match {
      case Nil => result
      case head :: tail => loop(tail, head :: result)
    }
    loop(values, Nil)
  }
  /** @param values input list
    * @return element count
    */
  @tailrec def length[A](values: List[A], count: Int = 0): Int = values match {
    case Nil => count
    case _ :: tail => length(tail, count + 1)
  }
  /** @param values input
    * @param count prefix size
    * @return prefix and suffix
    */
  def split[A](values: List[A], count: Int): (List[A], List[A]) = {
    @tailrec def loop(rest: List[A], remaining: Int, prefix: List[A]): (List[A], List[A]) =
      if (remaining == 0) (reverse(prefix), rest)
      else rest match {
        case Nil => (reverse(prefix), Nil)
        case head :: tail => loop(tail, remaining - 1, head :: prefix)
      }
    require(count >= 0, "Prefix size must be nonnegative")
    loop(values, count, Nil)
  }
  /** @param prefix reversed prefix
    * @param suffix suffix
    * @return concatenated list
    */
  @tailrec def prependReversed[A](prefix: List[A], suffix: List[A]): List[A] = prefix match {
    case Nil => suffix
    case head :: tail => prependReversed(tail, head :: suffix)
  }
}

/** Divide-and-conquer inversion counting. Equality is never an inversion. */
object Inversions {
  /** @param values integer sequence
    * @return inversion count, using Long to avoid Int overflow
    */
  def count(values: List[Int]): Long = sortAndCount(values)._2

  /** @param values sequence
    * @return sorted sequence and inversion count
    */
  def sortAndCount(values: List[Int]): (List[Int], Long) = {
    def solve(input: List[Int], size: Int): (List[Int], Long) = input match {
      case Nil => (Nil, 0L)
      case _ :: Nil => (input, 0L)
      case _ =>
        val half = size / 2
        val (left, right) = ListTools.split(input, half)
        val (sortedLeft, leftCount) = solve(left, half)
        val (sortedRight, rightCount) = solve(right, size - half)
        val (sorted, crossCount) = merge(sortedLeft, sortedRight, half)
        (sorted, leftCount + rightCount + crossCount)
    }
    solve(values, ListTools.length(values))
  }

  /** @param left sorted left half
    * @param right sorted right half
    * @param leftSize left length
    * @return merged list and cross-inversion count
    */
  private def merge(left: List[Int], right: List[Int], leftSize: Int): (List[Int], Long) = {
    @tailrec def loop(a: List[Int], b: List[Int], remaining: Int,
                      reversed: List[Int], total: Long): (List[Int], Long) = (a, b) match {
      case (Nil, _) => (ListTools.prependReversed(reversed, b), total)
      case (_, Nil) => (ListTools.prependReversed(reversed, a), total)
      case (x :: xs, y :: ys) =>
        if (x <= y) loop(xs, b, remaining - 1, x :: reversed, total)
        else loop(a, ys, remaining, y :: reversed, total + remaining.toLong)
    }
    loop(left, right, leftSize, Nil, 0L)
  }
}

/** Randomized three-way quicksort with explicitly threaded reproducible random state. */
object QuickSort {
  private sealed trait Task
  private final case class Sort(values: List[Int]) extends Task
  private final case class Emit(values: List[Int]) extends Task

  /** @param state previous random state
    * @return next 32-bit LCG state
    */
  private def nextState(state: Long): Long = (1664525L * state + 1013904223L) & 0xffffffffL

  /** @param values input
    * @param index zero-based index
    * @return indexed value
    */
  @tailrec private def at(values: List[Int], index: Int): Int = values match {
    case head :: tail => if (index == 0) head else at(tail, index - 1)
    case Nil => throw new IllegalArgumentException("Invalid pivot index")
  }

  /** @param values sequence
    * @param pivot pivot value
    * @return less, equal and greater groups
    */
  def partition3(values: List[Int], pivot: Int): (List[Int], List[Int], List[Int]) = {
    @tailrec def loop(rest: List[Int], less: List[Int], equal: List[Int], greater: List[Int])
      : (List[Int], List[Int], List[Int]) = rest match {
      case Nil => (ListTools.reverse(less), ListTools.reverse(equal), ListTools.reverse(greater))
      case head :: tail =>
        if (head < pivot) loop(tail, head :: less, equal, greater)
        else if (head == pivot) loop(tail, less, head :: equal, greater)
        else loop(tail, less, equal, head :: greater)
    }
    loop(values, Nil, Nil, Nil)
  }

  /** @param values input sequence
    * @param seed reproducible random seed
    * @return nondecreasing sequence; explicit immutable tasks avoid JVM stack overflow
    */
  def sort(values: List[Int], seed: Long = 20261011L): List[Int] = {
    @tailrec def run(tasks: List[Task], state: Long, reversed: List[Int]): List[Int] = tasks match {
      case Nil => ListTools.reverse(reversed)
      case Emit(items) :: rest => run(rest, state, ListTools.prependReversed(items, reversed))
      case Sort(Nil) :: rest => run(rest, state, reversed)
      case Sort((head :: Nil)) :: rest => run(rest, state, head :: reversed)
      case Sort(items) :: rest =>
        val updated = nextState(state)
        val pivot = at(items, (updated % ListTools.length(items)).toInt)
        val (less, equal, greater) = partition3(items, pivot)
        run(Sort(less) :: Emit(equal) :: Sort(greater) :: rest, updated, reversed)
    }
    run(Sort(values) :: Nil, seed & 0xffffffffL, Nil)
  }
}

/** Closest-pair divide and conquer, retaining y-order by linear merging. */
object ClosestPoints {
  private final case class Point(x: Int, y: Int)

  /** @param input coordinate pairs
    * @return validated internal points
    */
  private def parse(input: List[List[Int]]): List[Point] = {
    @tailrec def loop(rest: List[List[Int]], reversed: List[Point]): List[Point] = rest match {
      case Nil => ListTools.reverse(reversed)
      case (x :: y :: Nil) :: tail => loop(tail, Point(x, y) :: reversed)
      case _ => throw new IllegalArgumentException("Each point must contain exactly two integers")
    }
    loop(input, Nil)
  }
  /** @param a first point
    * @param b second point
    * @return squared Euclidean distance
    */
  private def squared(a: Point, b: Point): Double = {
    val dx = a.x.toDouble - b.x.toDouble
    val dy = a.y.toDouble - b.y.toDouble
    dx * dx + dy * dy
  }
  /** @param a first point
    * @param b second point
    * @param byX ordering axis
    * @return order predicate
    */
  private def before(a: Point, b: Point, byX: Boolean): Boolean =
    if (byX) a.x < b.x || (a.x == b.x && a.y <= b.y)
    else a.y < b.y || (a.y == b.y && a.x <= b.x)

  /** @param a sorted list
    * @param b sorted list
    * @param byX ordering axis
    * @return merged points
    */
  private def merge(a: List[Point], b: List[Point], byX: Boolean): List[Point] = {
    @tailrec def loop(left: List[Point], right: List[Point], reversed: List[Point]): List[Point] =
      (left, right) match {
        case (Nil, _) => ListTools.prependReversed(reversed, right)
        case (_, Nil) => ListTools.prependReversed(reversed, left)
        case (x :: xs, y :: ys) =>
          if (before(x, y, byX)) loop(xs, right, x :: reversed)
          else loop(left, ys, y :: reversed)
      }
    loop(a, b, Nil)
  }
  /** @param input points
    * @param size point count
    * @param byX ordering axis
    * @return sorted points
    */
  private def sortPoints(input: List[Point], size: Int, byX: Boolean): List[Point] = input match {
    case Nil => Nil
    case _ :: Nil => input
    case _ =>
      val half = size / 2
      val (left, right) = ListTools.split(input, half)
      merge(sortPoints(left, half, byX), sortPoints(right, size - half, byX), byX)
  }
  /** @param points points
    * @return brute-force squared minimum, infinity for fewer than two
    */
  private def brute(points: List[Point]): Double = {
    @tailrec def compare(point: Point, rest: List[Point], best: Double): Double = rest match {
      case Nil => best
      case head :: tail => compare(point, tail, math.min(best, squared(point, head)))
    }
    @tailrec def loop(rest: List[Point], best: Double): Double = rest match {
      case Nil => best
      case head :: tail => loop(tail, compare(head, tail, best))
    }
    loop(points, Double.PositiveInfinity)
  }
  /** @param points y-sorted points
    * @param middle dividing x coordinate
    * @param bound squared bound
    * @return y-sorted strip, constructed without filter
    */
  private def strip(points: List[Point], middle: Int, bound: Double): List[Point] = {
    @tailrec def loop(rest: List[Point], reversed: List[Point]): List[Point] = rest match {
      case Nil => ListTools.reverse(reversed)
      case head :: tail =>
        val dx = head.x.toDouble - middle.toDouble
        loop(tail, if (dx * dx <= bound) head :: reversed else reversed)
    }
    loop(points, Nil)
  }
  /** @param points y-sorted strip
    * @param initial squared bound
    * @return minimum squared distance
    */
  private def scan(points: List[Point], initial: Double): Double = {
    @tailrec def compare(point: Point, rest: List[Point], remaining: Int, best: Double): Double =
      if (remaining == 0) best
      else rest match {
        case Nil => best
        case head :: tail =>
          val dy = head.y.toDouble - point.y.toDouble
          if (dy * dy >= best) best
          else compare(point, tail, remaining - 1, math.min(best, squared(point, head)))
      }
    @tailrec def loop(rest: List[Point], best: Double): Double = rest match {
      case Nil => best
      case head :: tail => loop(tail, compare(head, tail, 7, best))
    }
    loop(points, initial)
  }
  /** @param input x-sorted points
    * @param size point count
    * @return squared minimum and y-sorted points
    */
  private def solve(input: List[Point], size: Int): (Double, List[Point]) =
    if (size <= 3) (brute(input), sortPoints(input, size, false))
    else {
      val half = size / 2
      val (left, right) = ListTools.split(input, half)
      val middle = right match {
        case head :: _ => head.x
        case Nil => throw new IllegalStateException("Empty right half")
      }
      val (leftDistance, leftY) = solve(left, half)
      val (rightDistance, rightY) = solve(right, size - half)
      val orderedY = merge(leftY, rightY, false)
      val bound = math.min(leftDistance, rightDistance)
      (if (bound == 0.0) 0.0 else scan(strip(orderedY, middle, bound), bound), orderedY)
    }

  /** @param coordinates list of two-integer coordinate lists, with at least two points
    * @return unrounded minimum distance; round only for presentation
    */
  def distance(coordinates: List[List[Int]]): Double = {
    val points = parse(coordinates)
    val size = ListTools.length(points)
    require(size >= 2, "At least two points are required")
    math.sqrt(solve(sortPoints(points, size, true), size)._1)
  }
}
