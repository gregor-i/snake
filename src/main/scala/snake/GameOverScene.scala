package snake

import indigo.{KeyboardEvent, SceneGraphNode, Text}
import indigo.scenes.{Lens, Scene, SceneEvent, SceneName}
import indigo.shared.constants.Keys
import indigo.shared.events.{EventFilters, GlobalEvent, MouseEvent}
import indigo.shared.scenegraph.SceneUpdateFragment
import indigo.shared.subsystems.SubSystem
import indigo.shared.{FrameContext, Outcome}

object GameOverScene extends Scene[StartUpData, GlobalModel, ViewModel] {
  type SceneModel     = SnakeModel
  type SceneViewModel = Unit

  val name: SceneName                                = SceneName("game over")
  val modelLens: Lens[GlobalModel, SceneModel]       = SnakeModel.modelLens
  val viewModelLens: Lens[ViewModel, SceneViewModel] = Lens.fixed(())
  val eventFilters: EventFilters                     = EventFilters.Default
  val subSystems: Set[SubSystem]                     = Set.empty

  def updateModel(context: FrameContext[StartUpData], model: SceneModel): GlobalEvent => Outcome[SceneModel] = {
    case KeyboardEvent.KeyDown(Keys.ENTER) | MouseEvent.Click(_, _) =>
      Outcome
        .pure(SnakeModel.initial)
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
    val centerX = Settings.textureSize * context.startUpData.width / 2
    SceneUpdateFragment.empty
      .addUiLayerNodes(GameMap.background(model)(context.startUpData))
      .addUiLayerNodes(
        Text("Game over", centerX, Settings.textureSize * 2 + 6, 1, Assets.fontKey).alignCenter,
        Text(s"final score: ${model.score}", centerX, Settings.textureSize * 5 + 6, 1, Assets.fontKey).alignCenter,
        Text("press Enter to restart", centerX, Settings.textureSize * (context.startUpData.height - 2) + 6, 1, Assets.fontKey).alignCenter
      )
  }

}
