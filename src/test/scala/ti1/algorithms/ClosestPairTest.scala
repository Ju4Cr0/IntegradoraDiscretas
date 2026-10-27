
package ti1

import ti1.utils.DataGenerator
import ti1.algorithms.ClosestPair
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

import scala.annotation.tailrec

class ClosestPairTest {

  private val algorithm = new ClosestPair()

  // Referencia independiente: compara todos los pares.
  private def referenceDistance(
                                 points: List[List[Int]]
                               ): Double = {

    def squared(a: List[Int], b: List[Int]): Double =
      (a, b) match {

        case (ax :: ay :: Nil, bx :: by :: Nil) =>
          val dx = ax.toDouble - bx.toDouble
          val dy = ay.toDouble - by.toDouble
          dx * dx + dy * dy

        case _ =>
          throw new IllegalArgumentException("Invalid point")
      }

    @tailrec
    def compare(
                 point: List[Int],
                 rest: List[List[Int]],
                 best: Double
               ): Double = rest match {

      case Nil => best

      case head :: tail =>
        compare(
          point,
          tail,
          math.min(best, squared(point, head))
        )
    }

    @tailrec
    def loop(
              rest: List[List[Int]],
              best: Double
            ): Double = rest match {

      case Nil => best

      case head :: tail =>
        loop(tail, compare(head, tail, best))
    }

    math.sqrt(loop(points, Double.PositiveInfinity))
  }

  private def checkPoints(points: List[List[Int]]): Unit = {

    val expected = referenceDistance(points)
    val actual = algorithm.distance(points)

    assertTrue(
      "Distance mismatch: " + points,
      math.abs(actual - expected) <=
        1e-10 * math.max(1.0, expected)
    )
  }

  private def rejectsInvalid(
                              points: List[List[Int]]
                            ): Boolean = {

    try {
      algorithm.distance(points)
      false
    } catch {
      case _: IllegalArgumentException => true
    }
  }

  @Test
  def testTriangle(): Unit = {

    assertEquals(
      5.0,
      algorithm.distance(
        List(List(0, 0), List(3, 4))
      ),
      0.0
    )
  }

  @Test
  def testReferenceCases(): Unit = {

    checkPoints(
      List(List(0, 0), List(3, 4), List(1, 1))
    )

    checkPoints(
      List(List(-4, 0), List(-1, 10),
        List(0, 10), List(4, 0))
    )

    checkPoints(
      List(List(0, 0), List(0, 10),
        List(0, 20), List(0, 21), List(0, 30))
    )

    checkPoints(
      List(List(1, 1), List(2, 2),
        List(1, 1), List(4, 4))
    )

    checkPoints(
      List(
        List(Int.MinValue, Int.MinValue),
        List(Int.MaxValue, Int.MaxValue)
      )
    )
  }

  @Test
  def test300PointDatasets(): Unit = {

    @tailrec
    def pointCases(index: Int): Unit = {

      if (index < 300) {

        checkPoints(
          DataGenerator.points(
            2 + index % 79,
            index.toLong + 1L,
            101
          )
        )

        pointCases(index + 1)
      }
    }

    pointCases(0)
  }

  @Test
  def testFewerThanTwoPoints(): Unit = {

    assertTrue(
      "Reject fewer than two points",
      rejectsInvalid(List(List(1, 2)))
    )
  }

  @Test
  def testMalformedPoint(): Unit = {

    assertTrue(
      "Reject malformed point",
      rejectsInvalid(
        List(List(1, 2), List(3))
      )
    )
  }

  @Test
  def testLargeDatasetKnownDistance(): Unit = {

    @tailrec
    def build(
               index: Int,
               acc: List[List[Int]]
             ): List[List[Int]] = {

      if (index == 10000) {
        acc
      } else {
        build(
          index + 1,
          List(index * 10, 0) :: acc
        )
      }
    }

    val points = List(1, 0) :: build(0, Nil)

    assertEquals(
      1.0,
      algorithm.distance(points),
      0.0
    )
  }

}
