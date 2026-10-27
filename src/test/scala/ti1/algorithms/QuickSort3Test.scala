
package ti1
import scala.annotation.tailrec
import ti1.utils.DataGenerator
import ti1.algorithms.QuickSort3
import org.junit.Test
import org.junit.Assert.assertEquals

class QuickSortTest {

  private val algorithm = new QuickSort3(20261011L)

  // Ordenamiento de referencia por inserción.
  private def referenceSort(values: List[Int]): List[Int] = {

    def insert(
                value: Int,
                sorted: List[Int]
              ): List[Int] = sorted match {

      case Nil =>
        value :: Nil

      case head :: tail =>
        if (value <= head)
          value :: sorted
        else
          head :: insert(value, tail)
    }

    values match {
      case Nil => Nil

      case head :: tail =>
        insert(head, referenceSort(tail))
    }
  }

  private def exhaustive(
                          depth: Int,
                          prefix: List[Int]
                        ): Int = {

    assertEquals(
      "Exhaustive quicksort",
      referenceSort(prefix),
      algorithm.sort(prefix)
    )

    if (depth == 0) 1
    else
      1 +
        exhaustive(depth - 1, -1 :: prefix) +
        exhaustive(depth - 1, 0 :: prefix) +
        exhaustive(depth - 1, 1 :: prefix)
  }

  @Test
  def testRepeatedExample(): Unit = {

    assertEquals(
      List(1, 1, 4, 4, 4, 4, 4, 9),
      algorithm.sort(List(4, 4, 1, 9, 4, 1, 4, 4))
    )
  }

  @Test
  def testExhaustive(): Unit = {
    assertEquals(3280, exhaustive(7, Nil))
  }

  @Test
  def testLargeEqualSequence(): Unit = {

    val repeated =
      DataGenerator.integers(100000, 42L, 1)

    assertEquals(
      repeated,
      algorithm.sort(repeated)
    )
  }

  @Test
  def testStatementExample(): Unit = {
    assertEquals(
      List(2, 2, 2, 3, 9),
      algorithm.sort(List(2, 3, 9, 2, 2))
    )
  }

  @Test
  def testLargeSortedSequence(): Unit = {


    def build(
               remaining: Int,
               acc: List[Int]
             ): List[Int] = {
      if (remaining == 0) acc
      else build(remaining - 1, remaining :: acc)
    }

    val sorted = build(100000, Nil)

    assertEquals(sorted, algorithm.sort(sorted))
  }

  @Test
  def testDifferentSeedsSameResult(): Unit = {
    val first = new QuickSort3(1L)
    val second = new QuickSort3(42L)

    val values = List(9, 2, 5, 2, -1, 8, 0, 9)

    val expected = List(-1, 0, 2, 2, 5, 8, 9, 9)

    assertEquals(expected, first.sort(values))
    assertEquals(expected, second.sort(values))
  }

  @Test
  def testExtremeValues(): Unit = {
    val values = List(
      Int.MaxValue,
      0,
      Int.MinValue,
      Int.MaxValue,
      -1,
      Int.MinValue
    )

    val expected = List(
      Int.MinValue,
      Int.MinValue,
      -1,
      0,
      Int.MaxValue,
      Int.MaxValue
    )

    assertEquals(expected, algorithm.sort(values))
  }

}
