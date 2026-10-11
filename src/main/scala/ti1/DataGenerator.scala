package ti1

import scala.annotation.tailrec

/** Pure reproducible input generators. */
object DataGenerator {
  /** @param seed state
    * @return next state
    */
  private def next(seed: Long): Long = (1664525L * seed + 1013904223L) & 0xffffffffL
  /** @param size input size
    * @param seed initial state
    * @param distinct number of values
    * @return pseudorandom integer list
    */
  def integers(size: Int, seed: Long, distinct: Int): List[Int] = {
    require(size >= 0 && distinct > 0, "Invalid generator parameters")
    @tailrec def loop(remaining: Int, state: Long, reversed: List[Int]): List[Int] =
      if (remaining == 0) ListTools.reverse(reversed)
      else {
        val updated = next(state)
        loop(remaining - 1, updated, ((updated % distinct).toInt - distinct / 2) :: reversed)
      }
    loop(size, seed & 0xffffffffL, Nil)
  }
  /** @param size list size
    * @return decreasing sequence
    */
  def descending(size: Int): List[Int] = {
    @tailrec def loop(value: Int, result: List[Int]): List[Int] =
      if (value > size) result else loop(value + 1, value :: result)
    loop(1, Nil)
  }
  /** @param size point count
    * @param seed initial state
    * @param range coordinate range
    * @return coordinate pairs
    */
  def points(size: Int, seed: Long, range: Int): List[List[Int]] = {
    require(size >= 0 && range > 0, "Invalid generator parameters")
    @tailrec def loop(remaining: Int, state: Long, reversed: List[List[Int]]): List[List[Int]] =
      if (remaining == 0) ListTools.reverse(reversed)
      else {
        val xState = next(state)
        val yState = next(xState)
        val point = List((xState % range).toInt - range / 2, (yState % range).toInt - range / 2)
        loop(remaining - 1, yState, point :: reversed)
      }
    loop(size, seed & 0xffffffffL, Nil)
  }
}
