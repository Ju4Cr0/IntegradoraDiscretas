package ti1

import java.io.BufferedReader
import java.io.FileReader
import java.io.PrintWriter
import scala.annotation.tailrec

/** Explicit effect boundary: file I/O is separate from the pure algorithms. */
object InputFiles {
  /** @param path file containing one integer per line
    * @return parsed list
    */
  def readIntegers(path: String): List[Int] = {
    val reader = new BufferedReader(new FileReader(path))
    @tailrec def loop(reversed: List[Int]): List[Int] = {
      val line = reader.readLine()
      if (line == null) ListTools.reverse(reversed)
      else loop(line.trim.toInt :: reversed)
    }
    try loop(Nil) finally reader.close()
  }
  /** @param values integers
    * @param path output file
    * @return Unit
    */
  def writeIntegers(values: List[Int], path: String): Unit = {
    val writer = new PrintWriter(path)
    @tailrec def loop(rest: List[Int]): Unit = rest match {
      case Nil => ()
      case head :: tail => writer.println(head); loop(tail)
    }
    try loop(values) finally writer.close()
  }
  /** @param values points
    * @param path output file
    * @return Unit
    */
  def writePoints(values: List[List[Int]], path: String): Unit = {
    val writer = new PrintWriter(path)
    @tailrec def loop(rest: List[List[Int]]): Unit = rest match {
      case Nil => ()
      case (x :: y :: Nil) :: tail => writer.println(s"$x $y"); loop(tail)
      case _ => throw new IllegalArgumentException("Invalid point")
    }
    try loop(values) finally writer.close()
  }
  /** @param path file containing x and y per line
    * @return parsed coordinates
    */
  def readPoints(path: String): List[List[Int]] = {
    val reader = new BufferedReader(new FileReader(path))
    @tailrec def loop(reversed: List[List[Int]]): List[List[Int]] = {
      val line = reader.readLine()
      if (line == null) ListTools.reverse(reversed)
      else {
        val parts = line.trim.split("\\s+")
        require(parts.length == 2, "Expected two coordinates per line")
        loop(List(parts(0).toInt, parts(1).toInt) :: reversed)
      }
    }
    try loop(Nil) finally reader.close()
  }
}
