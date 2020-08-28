package snake

import indigo.scenes.{Lens, Scene, SceneEvent, SceneName}
import indigo.shared.time.Seconds
import indigo._
import indigo.shared.constants.Keys
import snake.SnakeScene.SceneModel

import scala.util.chaining._

case class GameSceneModel(
    snakeHead: Point,
    snakeDirection: Direction,
    snakeBody: List[Point],
    snakeBodyLength: Int,
    target: Point,
    score: Int,
    gameOver: Boolean,
    lastUpdated: Seconds
)

object GameSceneModel {
  def initial = {
    val startPoint     = Point(GameMap.width / 2, GameMap.height / 2)
    val startDirection = Up
    val startLength    = 2
    GameSceneModel(
      snakeHead = startPoint,
      snakeDirection = startDirection,
      target = Point(GameMap.width / 3, GameMap.height / 2),
      snakeBody = List.tabulate(startLength)(i => startPoint - (startDirection.delta * (i + 1))),
      snakeBodyLength = startLength,
      score = 0,
      gameOver = false,
      lastUpdated = Seconds.zero
    )
  }

  val modelLens: Lens[GlobalModel, SceneModel] =
    Lens[GlobalModel, SceneModel](
      getter = _.snakeSceneModel,
      setter = (gameModel, sceneModel) => gameModel.copy(snakeSceneModel = sceneModel)
    )
}

object SnakeScene extends Scene[StartUpData, GlobalModel, ViewModel] {
  type SceneModel     = GameSceneModel
  type SceneViewModel = Unit

  val name: SceneName                                = SceneName("game")
  val modelLens: Lens[GlobalModel, SceneModel]       = GameSceneModel.modelLens
  val viewModelLens: Lens[ViewModel, SceneViewModel] = Lens.fixed(())
  val eventFilters: EventFilters                     = EventFilters.Default
  val subSystems: Set[SubSystem]                     = Set.empty

  def updateModel(context: FrameContext[StartUpData], model: SceneModel): GlobalEvent => Outcome[SceneModel] =
    if (model.gameOver) updateModelGameOver(context, model)
    else updateModelGameRunning(context, model)

  def updateModelGameOver(context: FrameContext[StartUpData], model: SceneModel): GlobalEvent => Outcome[SceneModel] = {
    case FrameTick if context.gameTime.running > model.lastUpdated + Settings.delayGameOver =>
      Outcome
        .pure(model)
        .addGlobalEvents(SceneEvent.JumpTo(GameOverScene.name))
    case _ => Outcome.pure(model)
  }

  def updateModelGameRunning(context: FrameContext[StartUpData], model: SceneModel): GlobalEvent => Outcome[SceneModel] = {
    case KeyboardEvent.KeyDown(Keys.UP_ARROW)    => model.pipe(turnHead(Up)).pipe(Outcome.pure)
    case KeyboardEvent.KeyDown(Keys.DOWN_ARROW)  => model.pipe(turnHead(Down)).pipe(Outcome.pure)
    case KeyboardEvent.KeyDown(Keys.LEFT_ARROW)  => model.pipe(turnHead(Left)).pipe(Outcome.pure)
    case KeyboardEvent.KeyDown(Keys.RIGHT_ARROW) => model.pipe(turnHead(Right)).pipe(Outcome.pure)

    case FrameTick if context.gameTime.running > model.lastUpdated + Settings.tickDelay =>
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
        snakeBodyLength = model.snakeBodyLength + 1,
        score = model.score + Settings.scorePerTarget
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
    _ => Outcome.pure(())

  def present(context: FrameContext[StartUpData], model: SceneModel, viewModel: SceneViewModel): SceneUpdateFragment =
    SceneUpdateFragment.empty
      .addGameLayerNodes(
        GameMap.graphics(model): _*
      )
      .addGameLayerNodes(
        Text(s"Score: ${model.score}", Settings.viewportWidth, 0, 0, Assets.fontKey).alignRight
      )

}
