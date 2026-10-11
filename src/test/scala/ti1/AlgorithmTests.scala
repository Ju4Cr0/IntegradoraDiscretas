package ti1

import scala.annotation.tailrec

/** Dependency-free unit and exhaustive differential tests. Run with sbt test. */
object AlgorithmTests {
  /** @param values sequence
    * @return reference inversion count
    */
  private def referenceInversions(values: List[Int]): Long = {
    @tailrec def countLess(value: Int, rest: List[Int], total: Long): Long = rest match {
      case Nil => total
      case head :: tail => countLess(value, tail, total + (if (value > head) 1L else 0L))
    }
    @tailrec def loop(rest: List[Int], total: Long): Long = rest match {
      case Nil => total
      case head :: tail => loop(tail, total + countLess(head, tail, 0L))
    }
    loop(values, 0L)
  }
  /** @param values input
    * @return independently insertion-sorted list
    */
  private def referenceSort(values: List[Int]): List[Int] = {
    def insert(value: Int, sorted: List[Int]): List[Int] = sorted match {
      case Nil => value :: Nil
      case head :: tail => if (value <= head) value :: sorted else head :: insert(value, tail)
    }
    values match {
      case Nil => Nil
      case head :: tail => insert(head, referenceSort(tail))
    }
  }
  /** @param points coordinates
    * @return exhaustive reference distance
    */
  private def referenceDistance(points: List[List[Int]]): Double = {
    def squared(a: List[Int], b: List[Int]): Double = (a, b) match {
      case (ax :: ay :: Nil, bx :: by :: Nil) =>
        val dx = ax.toDouble - bx.toDouble
        val dy = ay.toDouble - by.toDouble
        dx * dx + dy * dy
      case _ => throw new IllegalArgumentException("Invalid point")
    }
    @tailrec def compare(point: List[Int], rest: List[List[Int]], best: Double): Double = rest match {
      case Nil => best
      case head :: tail => compare(point, tail, math.min(best, squared(point, head)))
    }
    @tailrec def loop(rest: List[List[Int]], best: Double): Double = rest match {
      case Nil => best
      case head :: tail => loop(tail, compare(head, tail, best))
    }
    math.sqrt(loop(points, Double.PositiveInfinity))
  }
  /** @param expected expected value
    * @param actual actual value
    * @param label failure context
    */
  private def equal[A](expected: A, actual: A, label: String): Unit =
    assert(expected == actual, label + ": expected " + expected + ", obtained " + actual)

  /** @param points test points
    * @return Unit after distance assertion
    */
  private def checkPoints(points: List[List[Int]]): Unit = {
    val expected = referenceDistance(points)
    val actual = ClosestPoints.distance(points)
    assert(math.abs(actual - expected) <= 1e-10 * math.max(1.0, expected), "Distance mismatch: " + points)
  }
  /** @param depth remaining length
    * @param prefix generated prefix
    * @return checked sequence count
    */
  private def exhaustive(depth: Int, prefix: List[Int]): Int = {
    equal(referenceInversions(prefix), Inversions.count(prefix), "Exhaustive inversions")
    equal(referenceSort(prefix), QuickSort.sort(prefix), "Exhaustive quicksort")
    if (depth == 0) 1
    else 1 + exhaustive(depth - 1, -1 :: prefix) + exhaustive(depth - 1, 0 :: prefix) +
      exhaustive(depth - 1, 1 :: prefix)
  }
  /** @param args ignored
    * @return Unit; failures stop execution
    */
  def main(args: Array[String]): Unit = {
    equal(2L, Inversions.count(List(2, 3, 9, 2, 9)), "Statement example")
    equal(6L, Inversions.count(List(9, 7, 5, 3)), "Descending example")
    equal(List(1, 1, 4, 4, 4, 4, 4, 9), QuickSort.sort(List(4, 4, 1, 9, 4, 1, 4, 4)), "Repeated example")
    equal(5.0, ClosestPoints.distance(List(List(0, 0), List(3, 4))), "3-4-5 triangle")
    checkPoints(List(List(0, 0), List(3, 4), List(1, 1)))
    checkPoints(List(List(-4, 0), List(-1, 10), List(0, 10), List(4, 0)))
    checkPoints(List(List(0, 0), List(0, 10), List(0, 20), List(0, 21), List(0, 30)))
    checkPoints(List(List(1, 1), List(2, 2), List(1, 1), List(4, 4)))
    checkPoints(List(List(Int.MinValue, Int.MinValue), List(Int.MaxValue, Int.MaxValue)))
    val checked = exhaustive(7, Nil)
    @tailrec def pointCases(index: Int): Unit =
      if (index < 300) {
        checkPoints(DataGenerator.points(2 + index % 79, index.toLong + 1L, 101))
        pointCases(index + 1)
      }
    pointCases(0)
    val descending = DataGenerator.descending(100000)
    equal(4999950000L, Inversions.count(descending), "Count beyond Int range")
    val repeated = DataGenerator.integers(100000, 42L, 1)
    equal(repeated, QuickSort.sort(repeated), "Large equal sequence")
    var rejected = false
    try ClosestPoints.distance(List(List(1, 2))) catch {
      case _: IllegalArgumentException => rejected = true
    }
    assert(rejected, "Reject fewer than two points")
    rejected = false
    try ClosestPoints.distance(List(List(1, 2), List(3))) catch {
      case _: IllegalArgumentException => rejected = true
    }
    assert(rejected, "Reject malformed point")
    println("PASS: " + checked + " exhaustive sequences for each integer algorithm; 300 point datasets; examples, overflow, large repeats and invalid inputs.")
  }
}
