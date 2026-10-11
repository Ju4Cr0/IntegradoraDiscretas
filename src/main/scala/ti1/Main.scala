package ti1

/** Command-line demonstration and input-file runner. */
object Main {
  /** @param args optional algorithm and input path
    * @return Unit
    */
  def main(args: Array[String]): Unit = {
    if (args.length == 2) args(0) match {
      case "inversions" => println(Inversions.count(InputFiles.readIntegers(args(1))))
      case "quicksort" => println(QuickSort.sort(InputFiles.readIntegers(args(1))).mkString(" "))
      case "closest" => println("%.4f".formatLocal(java.util.Locale.US, ClosestPoints.distance(InputFiles.readPoints(args(1)))))
      case _ => throw new IllegalArgumentException("Use inversions, quicksort or closest")
    }
    else {
      println("Inversions: " + Inversions.count(List(2, 3, 9, 2, 9)))
      println("QuickSort: " + QuickSort.sort(List(2, 3, 9, 2, 2)).mkString(" "))
      println("Closest: %.4f".formatLocal(java.util.Locale.US, ClosestPoints.distance(List(List(0, 0), List(3, 4), List(1, 1)))))
    }
  }
}
