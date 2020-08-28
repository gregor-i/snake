package snake

import indigo.scenes.{Lens, Scene, SceneName}
import indigo.shared.time.Seconds
import indigo._

import scala.collection.immutable.Queue
import scala.util.Random

import scala.util.chaining._

case class SnakeSceneModel(
    snakeHead: Point,
    snakeDirection: Direction,
    snakeBody: List[Point],
    snakeBodyLength: Int,
    target: Point,
    gameOver: Boolean,
    lastUpdated: Seconds,
    tickDelay: Seconds
)

object SnakeSceneModel {
  def initial = {
    val startPoint     = Point(GameMap.width / 2, GameMap.height / 2)
    val startDirection = Up
    val startLength    = 2
    SnakeSceneModel(
      snakeHead = startPoint,
      snakeDirection = startDirection,
      target = Point(GameMap.width / 3, GameMap.height / 2),
      snakeBody = List.tabulate(startLength)(i => startPoint - (startDirection.delta * (i + 1))),
      snakeBodyLength = startLength,
      gameOver = false,
      lastUpdated = Seconds.zero,
      tickDelay = Seconds(0.2d)
    )
  }
}

case class SnakeSceneViewModel()

object SnakeScene extends Scene[StartUpData, GameModel, ViewModel] {
  type SceneModel     = SnakeSceneModel
  type SceneViewModel = SnakeSceneViewModel

  val name: SceneName = SceneName("Snake")
  val modelLens: Lens[GameModel, SceneModel] =
    Lens[GameModel, SceneModel](
      getter = _.snakeSceneModel,
      setter = (gameModel, sceneModel) => gameModel.copy(snakeSceneModel = sceneModel)
    )
  val viewModelLens: Lens[ViewModel, SceneViewModel] = Lens(getter = _ => SnakeSceneViewModel(), setter = (_, _) => ViewModel())
  val eventFilters: EventFilters                     = EventFilters.Default
  val subSystems: Set[SubSystem]                     = Set.empty

  def updateModel(context: FrameContext[StartUpData], model: SceneModel): GlobalEvent => Outcome[SceneModel] = {
    case _ if model.gameOver => Outcome.pure(model)
    case e: KeyboardEvent if e.keyCode == Keys.UP_ARROW =>
      model.pipe(turnHead(Up)).pipe(Outcome.pure)
    case e: KeyboardEvent if e.keyCode == Keys.DOWN_ARROW =>
      model.pipe(turnHead(Down)).pipe(Outcome.pure)
    case e: KeyboardEvent if e.keyCode == Keys.LEFT_ARROW =>
      model.pipe(turnHead(Left)).pipe(Outcome.pure)
    case e: KeyboardEvent if e.keyCode == Keys.RIGHT_ARROW =>
      model.pipe(turnHead(Right)).pipe(Outcome.pure)
    case FrameTick if context.gameTime.running > model.lastUpdated + model.tickDelay =>
      model
        .pipe(moveAhead)
        .pipe(handleTarget(context))
        .pipe(handleWalls)
        .pipe(handleBody)
        .pipe(_.copy(lastUpdated = context.gameTime.running))
        .pipe(Outcome.pure)

    case _ => Outcome(model)
  }

  def turnHead(newDirection: Direction)(model: SceneModel): SceneModel =
    model.copy(
      snakeDirection = newDirection
    )

  def moveAhead(model: SceneModel): SceneModel =
    model.copy(
      snakeHead = model.snakeDirection.delta + model.snakeHead,
      snakeBody = model.snakeBody.prepended(model.snakeHead).take(model.snakeBodyLength)
    )

  def handleTarget(context: FrameContext[_])(model: SceneModel): SceneModel =
    if (model.snakeHead == model.target) {
      // todo: this code does not terminate, if all tiles are filled with snakebody
      val newTarget = LazyList
        .continually {
          Point(context.dice.roll(GameMap.width - 2), context.dice.roll(GameMap.height - 2))
        }
        .filter(!model.snakeBody.contains(_))
        .filter(_ != model.snakeHead)
        .head
      model.copy(
        target = newTarget,
        snakeBodyLength = model.snakeBodyLength + 1
      )
    } else {
      model
    }

  def handleWalls(model: SceneModel): SceneModel =
    if (GameMap.wallPositions.contains(model.snakeHead)) {
      model.copy(gameOver = true)
    } else {
      model
    }

  def handleBody(model: SceneModel): SceneModel =
    if (model.snakeBody.contains(model.snakeHead)) {
      model.copy(gameOver = true)
    } else {
      model
    }

  def updateViewModel(
      context: FrameContext[StartUpData],
      model: SceneModel,
      viewModel: SceneViewModel
  ): GlobalEvent => Outcome[SceneViewModel] =
    _ => Outcome.pure(SnakeSceneViewModel())

  def present(context: FrameContext[StartUpData], model: SceneModel, viewModel: SceneViewModel): SceneUpdateFragment =
    SceneUpdateFragment.empty
      .addGameLayerNodes(
        GameMap.graphics(model): _*
      )

}
