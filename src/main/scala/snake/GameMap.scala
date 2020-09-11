package snake

import indigo.{Graphic, Point}

object GameMap {

  def allPositions(startUpData: StartUpData): List[Point] =
    (for {
      x <- 0 until startUpData.width
      y <- 0 until startUpData.height
    } yield Point(x, y)).toList

  def isWall(point: Point)(startUpData: StartUpData): Boolean =
    point.x == 0 || point.y == 0 || point.x == startUpData.width - 1 || point.y == startUpData.height - 1

  def wallPositions(startUpData: StartUpData): List[Point] = {
    allPositions(startUpData).filter(isWall(_)(startUpData))
  }

  def freePositions(model: SnakeModel)(startUpData: StartUpData): List[Point] =
    allPositions(startUpData)
      .filter(!isWall(_)(startUpData))
      .filter(!model.snakeBody.contains(_))
      .filter(_ != model.snakeHead)

  def graphics(model: SnakeModel)(startUpData: StartUpData): List[Graphic] = {
    def at(graphic: Graphic, point: Point): Graphic =
      graphic.moveBy(point * Settings.textureSize)

    val walls = List(
      List(
        at(Assets.cornerTopLeft, Point(0, 0)),
        at(Assets.cornerTopRight, Point(startUpData.width - 1, 0)),
        at(Assets.cornerBottomRight, Point(startUpData.width - 1, startUpData.height - 1)),
        at(Assets.cornerBottomLeft, Point(0, startUpData.height - 1))
      ),
      (for (x <- 1 until startUpData.width - 1) yield at(Assets.wallHorizontal, Point(x, 0))).toList,
      (for (x <- 1 until startUpData.width - 1) yield at(Assets.wallHorizontal, Point(x, startUpData.height - 1))).toList,
      (for (y <- 1 until startUpData.height - 1) yield at(Assets.wallVertical, Point(0, y))).toList,
      (for (y <- 1 until startUpData.height - 1) yield at(Assets.wallVertical, Point(startUpData.width - 1, y))).toList
    ).flatten

    val snakeHead = at(Assets.head(model.snakeDirection), model.snakeHead)

    val snakeBody = model.snakeBody.map(at(Assets.body, _))

    val target = at(Assets.target, model.target)

    target :: snakeHead :: (walls ++ snakeBody)
  }

  def background(model: SnakeModel)(startUpData: StartUpData): List[Graphic] =
    graphics(model)(startUpData)
      .map(_.withAlpha(0.7))
}
