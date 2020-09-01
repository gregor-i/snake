package snake

import indigo.scenes.{Lens, Scene, SceneEvent, SceneName}
import indigo.shared.events.{EventFilters, GlobalEvent, KeyboardEvent}
import indigo.shared.scenegraph.SceneUpdateFragment
import indigo.shared.subsystems.SubSystem
import indigo.shared.{FrameContext, Outcome}
import indigo._

object GreetingScene extends Scene[StartUpData, GlobalModel, ViewModel] {
  type SceneModel     = SnakeModel
  type SceneViewModel = Unit

  val name: SceneName                                = SceneName("greeting")
  val modelLens: Lens[GlobalModel, SceneModel]       = SnakeModel.modelLens
  val viewModelLens: Lens[ViewModel, SceneViewModel] = Lens.fixed(())
  val eventFilters: EventFilters                     = EventFilters.Default
  val subSystems: Set[SubSystem]                     = Set.empty

  def updateModel(context: FrameContext[StartUpData], model: SceneModel): GlobalEvent => Outcome[SceneModel] = {
    case KeyboardEvent.KeyDown(Keys.ENTER) | MouseEvent.Click(_, _) =>
      Outcome
        .pure(model)
        .addGlobalEvents(SceneEvent.JumpTo(GameScene.name))
    case _ => Outcome.pure(model)
  }

  def updateViewModel(
      context: FrameContext[StartUpData],
      model: SceneModel,
      viewModel: SceneViewModel
  ): GlobalEvent => Outcome[SceneViewModel] =
    _ => Outcome.pure(viewModel)

  def present(context: FrameContext[StartUpData], model: SceneModel, viewModel: SceneViewModel): SceneUpdateFragment = {
    val centerX = Settings.viewportWidth / 2
    SceneUpdateFragment.empty
      .addUiLayerNodes(GameMap.background(model))
      .addUiLayerNodes(
        Text("SNAKE", centerX, Settings.textureSize * 2 + 6, 1, Assets.fontKey).alignCenter,
        Text("controls:\nuse the arrow keys\nto change direction", centerX, Settings.textureSize * 5 + 8, 1, Assets.fontKey).alignCenter,
        Text("press Enter to start", centerX, Settings.textureSize * (GameMap.height - 2) + 6, 1, Assets.fontKey).alignCenter
      )
  }
}
