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
    def at(graphic: Graphic, point: Point): Graphic =
      graphic.moveBy(point * Settings.textureSize)

    val walls = List(
      List(
        at(Assets.cornerTopLeft, Point(0, 0)),
        at(Assets.cornerTopRight, Point(GameMap.width - 1, 0)),
        at(Assets.cornerBottomRight, Point(GameMap.width - 1, GameMap.height - 1)),
        at(Assets.cornerBottomLeft, Point(0, GameMap.height - 1))
      ),
      (for (x <- 1 until GameMap.width - 1) yield at(Assets.wallHorizontal, Point(x, 0))).toList,
      (for (x <- 1 until GameMap.width - 1) yield at(Assets.wallHorizontal, Point(x, GameMap.height - 1))).toList,
      (for (y <- 1 until GameMap.height - 1) yield at(Assets.wallVertical, Point(0, y))).toList,
      (for (y <- 1 until GameMap.height - 1) yield at(Assets.wallVertical, Point(GameMap.width - 1, y))).toList
    ).flatten

    val snakeHead = at(Assets.head(model.snakeDirection), model.snakeHead)

    val snakeBody = model.snakeBody.map(at(Assets.body, _))

    val target = at(Assets.target, model.target)

    target :: snakeHead :: (walls ++ snakeBody)
  }

  def background(model: SnakeModel): List[Graphic] =
    graphics(model)
      .map(_.withAlpha(0.7))
}
