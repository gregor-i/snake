package snake

import indigo.Point

object Settings {
  val textureSize = 8

  val width  = 12
  val height = 10

  val viewportWidth  = textureSize * width
  val viewportHeight = textureSize * height

}

object Arena {
  val wallPositions = {
    val top    = (0 until Settings.width).map(Point(_, 0))
    val bottom = (0 until Settings.width).map(Point(_, Settings.height - 1))
    val left   = (1 until Settings.height - 1).map(Point(0, _))
    val right  = (1 until Settings.height - 1).map(Point(Settings.width - 1, _))
    top ++ bottom ++ left ++ right
  }

}
