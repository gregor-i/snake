package snake

import indigo.scenes.Lens
import indigo.{FrameContext, Point, Seconds}

case class SnakeModel(
    snakeHead: Point,
    snakeDirection: Direction,
    snakeBody: List[Point],
    snakeBodyLength: Int,
    target: Point,
    score: Int,
    turnQueue: TurnQueue,
    lastUpdated: Seconds
) {
  def turnHead(newDirection: Direction): SnakeModel =
    copy(snakeDirection = newDirection)

  def setTurnQueue(turnQueue: TurnQueue): SnakeModel =
    copy(turnQueue = turnQueue)

  def setLastUpdated(lastUpdated: Seconds): SnakeModel =
    copy(lastUpdated = lastUpdated)

  def moveAhead: SnakeModel =
    copy(
      snakeHead = snakeDirection.delta + snakeHead,
      snakeBody = snakeBody.prepended(snakeHead).take(snakeBodyLength)
    )

  def turnHeadOrEnqueue(newDirection: Direction): SnakeModel =
    turnQueue match {
      case NoTurnYet                      => turnHead(newDirection).setTurnQueue(TurnHappened)
      case TurnHappened | TurnEnqueued(_) => setTurnQueue(TurnEnqueued(newDirection))
    }

  def handleQueue: SnakeModel =
    turnQueue match {
      case TurnEnqueued(direction) => turnHead(direction).setTurnQueue(TurnHappened)
      case _                       => setTurnQueue(NoTurnYet)
    }

  def handleTarget(context: FrameContext[_]): SnakeModel =
    if (snakeHead == target) {
      val freePositions = GameMap.freePositions(this)
      val newTarget =
        if (freePositions.isEmpty) Point(-1, -1)
        else freePositions(context.dice.rollFromZero(freePositions.length - 1))
      copy(
        target = newTarget,
        snakeBodyLength = snakeBodyLength + 1,
        score = score + Settings.scorePerTarget
      )
    } else {
      this
    }

  def gameOver: Boolean =
    GameMap.wallPositions.contains(snakeHead) || snakeBody.contains(snakeHead)
}

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
      getter = _.snakeModel,
      setter = (gameModel, sceneModel) => gameModel.copy(snakeModel = sceneModel)
    )
}

sealed trait TurnQueue
case object NoTurnYet                         extends TurnQueue
case object TurnHappened                      extends TurnQueue
case class TurnEnqueued(direction: Direction) extends TurnQueue
