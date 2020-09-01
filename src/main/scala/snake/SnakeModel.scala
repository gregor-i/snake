package snake

import indigo.scenes.Lens
import indigo.{Point, Seconds}

case class SnakeModel(
    snakeHead: Point,
    snakeDirection: Direction,
    snakeBody: List[Point],
    snakeBodyLength: Int,
    target: Point,
    score: Int,
    turnQueue: TurnQueue,
    lastUpdated: Seconds
)

object SnakeModel {
  val initial: SnakeModel = {
    val startPoint     = Point(6, 3)
    val startDirection = Left
    val startLength    = 2
    SnakeModel(
      snakeHead = startPoint,
      snakeDirection = startDirection,
      target = Point(3, 3),
      snakeBody = List.tabulate(startLength)(i => startPoint - (startDirection.delta * (i + 1))),
      snakeBodyLength = startLength,
      score = 0,
      turnQueue = NoTurnYet,
      lastUpdated = Seconds.zero
    )
  }

  val modelLens: Lens[GlobalModel, SnakeModel] =
    Lens[GlobalModel, SnakeModel](
      getter = _.snakeSceneModel,
      setter = (gameModel, sceneModel) => gameModel.copy(snakeSceneModel = sceneModel)
    )
}

sealed trait TurnQueue
case object NoTurnYet                         extends TurnQueue
case object TurnHappened                      extends TurnQueue
case class TurnEnqueued(direction: Direction) extends TurnQueue
