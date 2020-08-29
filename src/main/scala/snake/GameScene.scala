package snake

import indigo._
import indigo.scenes.{Lens, Scene, SceneEvent, SceneName}
import indigo.shared.constants.Keys

import scala.util.chaining._

object GameScene extends Scene[StartUpData, GlobalModel, ViewModel] {
  type SceneModel     = SnakeModel
  type SceneViewModel = Unit

  val name: SceneName                                = SceneName("game")
  val modelLens: Lens[GlobalModel, SceneModel]       = SnakeModel.modelLens
  val viewModelLens: Lens[ViewModel, SceneViewModel] = Lens.fixed(())
  val eventFilters: EventFilters                     = EventFilters.Default
  val subSystems: Set[SubSystem]                     = Set.empty

  def updateModel(context: FrameContext[StartUpData], model: SceneModel): GlobalEvent => Outcome[SceneModel] =
    updateModelGameRunning(context, model)

  def updateModelGameRunning(context: FrameContext[StartUpData], model: SceneModel): GlobalEvent => Outcome[SceneModel] = {
    case KeyboardEvent.KeyDown(Keys.UP_ARROW)    => model.pipe(turnHead(Up)).pipe(Outcome.pure)
    case KeyboardEvent.KeyDown(Keys.DOWN_ARROW)  => model.pipe(turnHead(Down)).pipe(Outcome.pure)
    case KeyboardEvent.KeyDown(Keys.LEFT_ARROW)  => model.pipe(turnHead(Left)).pipe(Outcome.pure)
    case KeyboardEvent.KeyDown(Keys.RIGHT_ARROW) => model.pipe(turnHead(Right)).pipe(Outcome.pure)

    case FrameTick if context.gameTime.running > model.lastUpdated + Settings.tickDelay =>
      model
        .pipe(moveAhead)
        .pipe(handleTarget(context))
        .pipe(_.copy(lastUpdated = context.gameTime.running))
        .pipe {
          case model if gameOver(model) =>
            Outcome(model)
              .addGlobalEvents(SceneEvent.JumpTo(GameOverScene.name))
          case model =>
            Outcome(model)
        }

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
      val freePositions = GameMap.freePositions(model)
      val newTarget =
        if (freePositions.isEmpty) Point(-1, -1)
        else freePositions(context.dice.rollFromZero(freePositions.length - 1))
      model.copy(
        target = newTarget,
        snakeBodyLength = model.snakeBodyLength + 1,
        score = model.score + Settings.scorePerTarget
      )
    } else {
      model
    }

  def gameOver(model: SceneModel): Boolean =
    GameMap.wallPositions.contains(model.snakeHead) || model.snakeBody.contains(model.snakeHead)

  def updateViewModel(
      context: FrameContext[StartUpData],
      model: SceneModel,
      viewModel: SceneViewModel
  ): GlobalEvent => Outcome[SceneViewModel] =
    _ => Outcome.pure(())

  def present(context: FrameContext[StartUpData], model: SceneModel, viewModel: SceneViewModel): SceneUpdateFragment =
    SceneUpdateFragment.empty
      .addGameLayerNodes(GameMap.graphics(model))
      .addGameLayerNodes(
        Text(s"Score: ${model.score}", Settings.viewportWidth, 0, 0, Assets.fontKey).alignRight
      )

}
