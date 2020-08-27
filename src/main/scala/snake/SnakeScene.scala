package snake

import indigo.scenes.{Lens, Scene, SceneName}
import indigo.shared.time.Seconds
import indigo._

case class SnakeSceneModel(
    snakeHead: Point = Point(Settings.width / 2, Settings.height / 2),
    snakeDirection: Direction = Up,
    lastUpdated: Seconds = Seconds.zero,
    tickDelay: Seconds = Seconds(1d)
)

sealed trait Direction
case object Up    extends Direction
case object Right extends Direction
case object Down  extends Direction
case object Left  extends Direction

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
    case e: KeyboardEvent if e.keyCode == Keys.UP_ARROW =>
      Outcome(model.copy(snakeDirection = Up))
    case e: KeyboardEvent if e.keyCode == Keys.DOWN_ARROW =>
      Outcome(model.copy(snakeDirection = Down))
    case e: KeyboardEvent if e.keyCode == Keys.LEFT_ARROW =>
      Outcome(model.copy(snakeDirection = Left))
    case e: KeyboardEvent if e.keyCode == Keys.RIGHT_ARROW =>
      Outcome(model.copy(snakeDirection = Right))
    case FrameTick if context.gameTime.running > model.lastUpdated + model.tickDelay =>
      val newPos = model.snakeDirection match {
        case Up    => model.snakeHead + Point(0, -1)
        case Down  => model.snakeHead + Point(0, +1)
        case Right => model.snakeHead + Point(+1, 0)
        case Left  => model.snakeHead + Point(-1, 0)
      }

      Outcome(model.copy(snakeHead = newPos, lastUpdated = context.gameTime.running))

    case _ => Outcome(model)
  }

  def updateViewModel(
      context: FrameContext[StartUpData],
      model: SceneModel,
      viewModel: SceneViewModel
  ): GlobalEvent => Outcome[SceneViewModel] =
    event => Outcome.pure(SnakeSceneViewModel())

  def present(context: FrameContext[StartUpData], model: SceneModel, viewModel: SceneViewModel): SceneUpdateFragment =
    SceneUpdateFragment.empty
      .addGameLayerNodes(
        Arena.wallPositions.map(p => Assets.wall.moveBy(p * Settings.textureSize)): _*
      )
      .addGameLayerNodes(
        Assets.head(model.snakeDirection).moveBy(model.snakeHead * Settings.textureSize)
      )
}
