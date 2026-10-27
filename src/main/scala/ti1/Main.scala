package ti1

import ti1.algorithms.{ClosestPair, CountingInversions, QuickSort3}
import ti1.utils.InputFiles

/** Command-line entry point.
  *
  * Without arguments it prints the assignment examples. With two arguments it runs one
  * algorithm on an input file:
  * {{{
  * inversions <file>   one integer per line
  * quicksort  <file>   one integer per line
  * closest    <file>   "x y" per line
  * }}}
  */
object Main {
  private val inversions = new CountingInversions()
  private val quickSort = new QuickSort3(20261011L)
  private val closestPair = new ClosestPair()

  /** @param args nothing, or an algorithm name followed by an input file path */
  def main(args: Array[String]): Unit = args.toList match {
    case Nil =>
      println("Inversions: " + inversions.count(List(2, 3, 9, 2, 9)))
      println("QuickSort: " + quickSort.sort(List(2, 3, 9, 2, 2)).mkString(" "))
      println("Closest: " + format(closestPair.distance(List(List(0, 0), List(3, 4), List(1, 1)))))
    case "inversions" :: path :: Nil =>
      println(inversions.count(InputFiles.readIntegers(path)))
    case "quicksort" :: path :: Nil =>
      println(quickSort.sort(InputFiles.readIntegers(path)).mkString(" "))
    case "closest" :: path :: Nil =>
      println(format(closestPair.distance(InputFiles.readPoints(path))))
    case _ =>
      println("Usage: Main [inversions | quicksort | closest] <file>")
  }

  /** @param distance value to show
    * @return the distance with four decimal digits, using a dot as decimal separator
    */
  private def format(distance: Double): String = "%.4f".formatLocal(java.util.Locale.US, distance)
}
