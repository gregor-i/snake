package snake

import indigo._
import indigo.scenes.{Lens, Scene, SceneEvent, SceneName}
import indigo.shared.constants.Keys
import org.scalajs.dom

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
    case KeyboardEvent.KeyDown(Keys.UP_ARROW)    => model.pipe(turnHeadOrEnqueue(Up)).pipe(Outcome.pure)
    case KeyboardEvent.KeyDown(Keys.DOWN_ARROW)  => model.pipe(turnHeadOrEnqueue(Down)).pipe(Outcome.pure)
    case KeyboardEvent.KeyDown(Keys.LEFT_ARROW)  => model.pipe(turnHeadOrEnqueue(Left)).pipe(Outcome.pure)
    case KeyboardEvent.KeyDown(Keys.RIGHT_ARROW) => model.pipe(turnHeadOrEnqueue(Right)).pipe(Outcome.pure)

    case MouseEvent.Click(x, y) =>
      val canvas = dom.document.querySelector("#indigo-container canvas")
      val bounds = canvas.getBoundingClientRect()
      val px     = (x - bounds.left) / bounds.width
      val py     = (y - bounds.top) / bounds.height

      val direction = (px > py, px > 1d - py) match {
        case (true, true)   => Right
        case (true, false)  => Up
        case (false, true)  => Down
        case (false, false) => Left
      }

      model.pipe(turnHeadOrEnqueue(direction)).pipe(Outcome.pure)

    case FrameTick if context.gameTime.running > model.lastUpdated + Settings.tickDelay =>
      model
        .pipe(moveAhead)
        .pipe(handleQueue)
        .pipe(handleTarget(context))
        .copy(lastUpdated = context.gameTime.running)
        .pipe(setTurnQueue(NoTurnYet))
        .pipe {
          case model if gameOver(model) =>
            Outcome(model)
              .addGlobalEvents(SceneEvent.JumpTo(GameOverScene.name))
          case model =>
            Outcome(model)
        }

    case _ => Outcome(model)
  }

  def turnHeadOrEnqueue(newDirection: Direction)(model: SceneModel): SceneModel =
    model.turnQueue match {
      case NoTurnYet                      => model.pipe(turnHead(newDirection)).pipe(setTurnQueue(TurnHappened))
      case TurnHappened | TurnEnqueued(_) => model.pipe(setTurnQueue(TurnEnqueued(newDirection)))
    }

  def turnHead(newDirection: Direction)(model: SceneModel): SceneModel =
    model.copy(snakeDirection = newDirection)

  def setTurnQueue(turnQueue: TurnQueue)(model: SceneModel): SceneModel =
    model.copy(turnQueue = turnQueue)

  def moveAhead(model: SceneModel): SceneModel =
    model.copy(
      snakeHead = model.snakeDirection.delta + model.snakeHead,
      snakeBody = model.snakeBody.prepended(model.snakeHead).take(model.snakeBodyLength)
    )

  def handleQueue(model: SceneModel): SceneModel =
    model.turnQueue match {
      case TurnEnqueued(direction) => turnHead(direction)(model).copy(turnQueue = TurnHappened)
      case _                       => model.copy(turnQueue = NoTurnYet)
    }

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
