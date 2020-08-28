package snake

import indigo.Point

sealed trait Direction {
  def delta: Point = this match {
    case Up    => Point(0, -1)
    case Down  => Point(0, +1)
    case Right => Point(+1, 0)
    case Left  => Point(-1, 0)
  }

  def move(point: Point): Point     = point + delta
  def moveBack(point: Point): Point = point - delta
}
case object Up    extends Direction
case object Right extends Direction
case object Down  extends Direction
case object Left  extends Direction
