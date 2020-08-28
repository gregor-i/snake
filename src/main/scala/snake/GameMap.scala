package snake

import indigo.{Graphic, Point}

object GameMap {
  val width  = 12
  val height = 10

  val wallPositions: Seq[Point] = {
    val top    = (0 until width).map(Point(_, 0))
    val bottom = (0 until width).map(Point(_, height - 1))
    val left   = (1 until height - 1).map(Point(0, _))
    val right  = (1 until height - 1).map(Point(width - 1, _))
    top ++ bottom ++ left ++ right
  }

  def graphics(model: SnakeSceneModel): Seq[Graphic] = {
    val walls = wallPositions.map(p => Assets.wall.moveBy(p * Settings.textureSize))

    val snakeHead = Assets.head(model.snakeDirection).moveBy(model.snakeHead * Settings.textureSize)

    val snakeBody = model.snakeBody.toSeq.map(p => Assets.body.moveBy(p * Settings.textureSize))

    val target = Assets.target.moveBy(model.target * Settings.textureSize)

    target +: snakeHead +: (walls ++ snakeBody)
  }
}
