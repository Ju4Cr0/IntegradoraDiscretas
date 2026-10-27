
package ti1

import ti1.utils.DataGenerator
import ti1.algorithms.CountingInversions
import org.junit.Test
import org.junit.Assert.assertEquals

import scala.annotation.tailrec

class CountingInversionsTest {

  private val algorithm = new CountingInversions()

  // Referencia independiente: cuenta cada par invertido.
  private def referenceInversions(values: List[Int]): Long = {

    @tailrec
    def countLess(
                   value: Int,
                   rest: List[Int],
                   total: Long
                 ): Long = rest match {
      case Nil => total

      case head :: tail =>
        countLess(
          value,
          tail,
          total + (if (value > head) 1L else 0L)
        )
    }

    @tailrec
    def loop(rest: List[Int], total: Long): Long =
      rest match {
        case Nil => total

        case head :: tail =>
          loop(tail, total + countLess(head, tail, 0L))
      }

    loop(values, 0L)
  }

  // Prueba todas las secuencias de longitud 0 a 7.
  private def exhaustive(
                          depth: Int,
                          prefix: List[Int]
                        ): Int = {

    assertEquals(
      "Exhaustive inversions",
      referenceInversions(prefix),
      algorithm.count(prefix)
    )

    if (depth == 0) 1
    else
      1 +
        exhaustive(depth - 1, -1 :: prefix) +
        exhaustive(depth - 1, 0 :: prefix) +
        exhaustive(depth - 1, 1 :: prefix)
  }

  @Test
  def testStatementExample(): Unit = {
    assertEquals(
      2L,
      algorithm.count(List(2, 3, 9, 2, 9))
    )
  }

  @Test
  def testDescendingExample(): Unit = {
    assertEquals(
      6L,
      algorithm.count(List(9, 7, 5, 3))
    )
  }

  @Test
  def testExhaustive(): Unit = {
    assertEquals(3280, exhaustive(7, Nil))
  }

  @Test
  def testCountBeyondIntRange(): Unit = {
    val descending = DataGenerator.descending(100000)

    assertEquals(
      4999950000L,
      algorithm.count(descending)
    )
  }

  @Test
  def testLargeAscendingSequence(): Unit = {

    @tailrec
    def build(
               remaining: Int,
               acc: List[Int]
             ): List[Int] = {
      if (remaining == 0) acc
      else build(remaining - 1, remaining :: acc)
    }

    val ascending = build(100000, Nil)

    assertEquals(0L, algorithm.count(ascending))
  }

  @Test
  def testLargeEqualSequence(): Unit = {
    val repeated = DataGenerator.integers(100000, 42L, 1)

    assertEquals(0L, algorithm.count(repeated))
  }

  @Test
  def testExtremeValues(): Unit = {
    assertEquals(
      1L,
      algorithm.count(List(Int.MaxValue, Int.MinValue))
    )
  }

}
