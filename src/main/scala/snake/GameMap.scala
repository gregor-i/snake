package snake

import indigo.{Graphic, Point}

object GameMap {
  val width  = 12
  val height = 10

  val allPositions: List[Point] =
    (for {
      x <- 0 until width
      y <- 0 until height
    } yield Point(x, y)).toList

  def isWall(point: Point): Boolean =
    point.x == 0 || point.y == 0 || point.x == width - 1 || point.y == height - 1

  val wallPositions: List[Point] = {
    allPositions.filter(isWall)
  }

  def freePositions(model: SnakeModel): List[Point] =
    allPositions
      .filter(!isWall(_))
      .filter(!model.snakeBody.contains(_))
      .filter(_ != model.snakeHead)

  def graphics(model: SnakeModel): List[Graphic] = {
    val walls = wallPositions.map(p => Assets.wall.moveBy(p * Settings.textureSize))

    val snakeHead = Assets.head(model.snakeDirection).moveBy(model.snakeHead * Settings.textureSize)

    val snakeBody = model.snakeBody.map(p => Assets.body.moveBy(p * Settings.textureSize))

    val target = Assets.target.moveBy(model.target * Settings.textureSize)

    target :: snakeHead :: (walls ++ snakeBody)
  }

  def background(model: SnakeModel): List[Graphic] =
    graphics(model).map(_.withAlpha(0.7))
}
