package snake

import indigo.scenes.{Lens, Scene, SceneEvent, SceneName}
import indigo.shared.events.{EventFilters, GlobalEvent, KeyboardEvent}
import indigo.shared.scenegraph.SceneUpdateFragment
import indigo.shared.subsystems.SubSystem
import indigo.shared.{FrameContext, Outcome}
import indigo._

object GreetingScene extends Scene[StartUpData, GlobalModel, ViewModel] {
  type SceneModel     = GameSceneModel
  type SceneViewModel = Unit

  val name: SceneName                                = SceneName("greeting")
  val modelLens: Lens[GlobalModel, SceneModel]       = GameSceneModel.modelLens
  val viewModelLens: Lens[ViewModel, SceneViewModel] = Lens.fixed(())
  val eventFilters: EventFilters                     = EventFilters.Default
  val subSystems: Set[SubSystem]                     = Set.empty

  def updateModel(context: FrameContext[StartUpData], model: SceneModel): GlobalEvent => Outcome[SceneModel] = {
    case KeyboardEvent.KeyDown(Keys.ENTER) =>
      Outcome
        .pure(model)
        .addGlobalEvents(SceneEvent.JumpTo(SnakeScene.name))
    case _ => Outcome.pure(model)
  }

  def updateViewModel(
      context: FrameContext[StartUpData],
      model: SceneModel,
      viewModel: SceneViewModel
  ): GlobalEvent => Outcome[SceneViewModel] =
    _ => Outcome.pure(viewModel)

  def present(context: FrameContext[StartUpData], model: SceneModel, viewModel: SceneViewModel): SceneUpdateFragment =
    SceneUpdateFragment.empty
      .addUiLayerNodes(drawControlsText(Settings.viewportWidth / 2, Settings.viewportHeight / 2))

  def drawControlsText(centerX: Int, centerY: Int): List[SceneGraphNode] =
    List(
      Text("SNAKE", centerX, centerY - 50, 1, Assets.fontKey).alignCenter,
      Text("controls:\nuse the arrow keys\nto change direction", 24, centerY - 15, 1, Assets.fontKey).alignLeft,
      Text("press Enter to start", centerX, centerY + 40, 1, Assets.fontKey).alignCenter
    )
}
