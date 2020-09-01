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
    case KeyboardEvent.KeyDown(Keys.UP_ARROW)    => model.turnHeadOrEnqueue(Up).pipe(Outcome.pure)
    case KeyboardEvent.KeyDown(Keys.DOWN_ARROW)  => model.turnHeadOrEnqueue(Down).pipe(Outcome.pure)
    case KeyboardEvent.KeyDown(Keys.LEFT_ARROW)  => model.turnHeadOrEnqueue(Left).pipe(Outcome.pure)
    case KeyboardEvent.KeyDown(Keys.RIGHT_ARROW) => model.turnHeadOrEnqueue(Right).pipe(Outcome.pure)

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

      model.turnHeadOrEnqueue(direction).pipe(Outcome.pure)

    case FrameTick if context.gameTime.running > model.lastUpdated + Settings.tickDelay =>
      model.moveAhead.handleQueue
        .handleTarget(context)
        .setLastUpdated(context.gameTime.running)
        .setTurnQueue(NoTurnYet)
        .pipe {
          case model if model.gameOver =>
            Outcome(model)
              .addGlobalEvents(SceneEvent.JumpTo(GameOverScene.name))
          case model =>
            Outcome(model)
        }

    case _ => Outcome(model)
  }

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
