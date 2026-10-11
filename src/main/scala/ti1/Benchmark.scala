package ti1

import java.io.PrintWriter
import scala.annotation.tailrec

/** Reproducible timing harness; effects are isolated outside algorithm implementations. */
object Benchmark {
  @volatile private var sink: Long = 0L
  /** @param name algorithm
    * @param integers integer input
    * @param points point input
    * @return Unit
    */
  private def execute(name: String, integers: List[Int], points: List[List[Int]]): Unit = name match {
    case "inversions" => sink = Inversions.count(integers)
    case "quicksort" => sink = ListTools.length(QuickSort.sort(integers)).toLong
    case "closest" => sink = java.lang.Double.doubleToRawLongBits(ClosestPoints.distance(points))
    case _ => throw new IllegalArgumentException("Unknown algorithm")
  }
  /** @param n input size
    * @return size category as defined in the assignment
    */
  private def category(n: Int): String =
    if (n < 100) "Toy"
    else if (n < 10000) "Small"
    else if (n < 100000) "Medium"
    else if (n >= 1000000) "Large"
    else "Medium-Large"

  /** @param rest sizes to label
    * @param reversed labelled sizes in reverse order
    * @return sizes paired with their category
    */
  @tailrec private def withCategories(rest: List[Int], reversed: List[(Int, String)]): List[(Int, String)] = rest match {
    case Nil => ListTools.reverse(reversed)
    case head :: tail => withCategories(tail, (head, category(head)) :: reversed)
  }

  /** @param rest labelled sizes
    * @return comma-separated sizes
    */
  private def sizeList(rest: List[(Int, String)]): String = rest match {
    case Nil => ""
    case (size, _) :: Nil => size.toString
    case (size, _) :: tail => s"$size, ${sizeList(tail)}"
  }

  /** @return processor description reported by the operating system */
  private def cpuName(): String = {
    val windows = System.getenv("PROCESSOR_IDENTIFIER")
    if (windows != null) windows else System.getProperty("os.arch")
  }

  /** @return total physical memory in bytes, or 0 if the JVM cannot report it */
  private def physicalMemory(): Long = java.lang.management.ManagementFactory.getOperatingSystemMXBean match {
    case bean: com.sun.management.OperatingSystemMXBean => bean.getTotalMemorySize
    case _ => 0L
  }

  /** @param args ignored; outputs raw measurements, means and runtime information
    * @return Unit
    */
  def main(args: Array[String]): Unit = {
    val sizes = withCategories(List(16, 64, 1024, 8192, 16384, 65536, 1048576), Nil)
    val raw = new PrintWriter("data/raw.csv")
    val means = new PrintWriter("data/means.csv")
    val environment = new PrintWriter("data/environment.txt")
    environment.println("Recorded UTC: " + java.time.Instant.now())
    environment.println("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version") + " " + System.getProperty("os.arch"))
    environment.println("Java: " + System.getProperty("java.runtime.version"))
    environment.println("Available logical processors: " + Runtime.getRuntime.availableProcessors())
    environment.println("CPU: " + cpuName())
    environment.println("Physical RAM (GiB): " + "%.1f".formatLocal(java.util.Locale.US, physicalMemory() / 1073741824.0))
    environment.println("Maximum JVM heap (MiB): " + Runtime.getRuntime.maxMemory() / 1048576L)
    environment.println("Scala: " + scala.util.Properties.versionNumberString)
    environment.println("Additional warmups: 5 per dataset; recorded runs: 11; discarded: run 0; averaged: runs 1..10")
    environment.println("Seed: 20261011; sizes: " + sizeList(sizes))
    environment.close()
    raw.println("algorithm,scenario,category,n,run,kept,milliseconds")
    means.println("algorithm,scenario,category,n,mean_ms")
    @tailrec def runs(name: String, scenario: String, category: String, size: Int,
                      ints: List[Int], points: List[List[Int]], run: Int, sum: Double): Double =
      if (run == 11) sum / 10.0
      else {
        val start = System.nanoTime()
        execute(name, ints, points)
        val elapsed = (System.nanoTime() - start).toDouble / 1000000.0
        raw.println(name + "," + scenario + "," + category + "," + size + "," + run + "," + (run > 0) + "," + elapsed)
        runs(name, scenario, category, size, ints, points, run + 1, sum + (if (run > 0) elapsed else 0.0))
      }
    @tailrec def warm(name: String, ints: List[Int], points: List[List[Int]], remaining: Int): Unit =
      if (remaining > 0) { execute(name, ints, points); warm(name, ints, points, remaining - 1) }
    @tailrec def datasets(name: String, scenario: String, remaining: List[(Int, String)]): Unit = remaining match {
      case Nil => ()
      case (size, category) :: tail =>
        val ints = if (scenario == "descending") DataGenerator.descending(size)
          else DataGenerator.integers(size, 20261011L, if (scenario == "duplicates") 5 else size * 8)
        val points = if (name == "closest") DataGenerator.points(size, 20261011L, 1000000000) else Nil
        if (name == "closest") {
          InputFiles.writePoints(points, "data/input-closest-" + size + ".txt")
          require(InputFiles.readPoints("data/input-closest-" + size + ".txt") == points)
        } else {
          InputFiles.writeIntegers(ints, "data/input-" + name + "-" + scenario + "-" + size + ".txt")
          require(InputFiles.readIntegers("data/input-" + name + "-" + scenario + "-" + size + ".txt") == ints)
        }
        warm(name, ints, points, 5)
        val average = runs(name, scenario, category, size, ints, points, 0, 0.0)
        means.println(name + "," + scenario + "," + category + "," + size + "," + average)
        println(name + " / " + scenario + " / " + size + " -> " + average + " ms")
        datasets(name, scenario, tail)
    }
    try {
      datasets("inversions", "random", sizes)
      datasets("inversions", "descending", sizes)
      datasets("quicksort", "random", sizes)
      datasets("quicksort", "duplicates", sizes)
      datasets("closest", "random", sizes)
    } finally { raw.close(); means.close() }
  }
}
