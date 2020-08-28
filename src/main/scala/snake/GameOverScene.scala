package snake

import indigo.{KeyboardEvent, SceneGraphNode, Text}
import indigo.scenes.{Lens, Scene, SceneEvent, SceneName}
import indigo.shared.constants.Keys
import indigo.shared.events.{EventFilters, GlobalEvent}
import indigo.shared.scenegraph.SceneUpdateFragment
import indigo.shared.subsystems.SubSystem
import indigo.shared.{FrameContext, Outcome}

object GameOverScene extends Scene[StartUpData, GlobalModel, ViewModel] {
  type SceneModel     = GameSceneModel
  type SceneViewModel = Unit

  val name: SceneName                                = SceneName("game over")
  val modelLens: Lens[GlobalModel, SceneModel]       = GameSceneModel.modelLens
  val viewModelLens: Lens[ViewModel, SceneViewModel] = Lens.fixed(())
  val eventFilters: EventFilters                     = EventFilters.Default
  val subSystems: Set[SubSystem]                     = Set.empty

  def updateModel(context: FrameContext[StartUpData], model: SceneModel): GlobalEvent => Outcome[SceneModel] = {
    case KeyboardEvent.KeyDown(Keys.ENTER) =>
      Outcome
        .pure(GameSceneModel.initial)
        .addGlobalEvents(SceneEvent.JumpTo(SnakeScene.name))
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
    val centerY = Settings.viewportHeight / 2
    SceneUpdateFragment.empty
      .addUiLayerNodes(
        Text("Game over", centerX, centerY - 50, 1, Assets.fontKey).alignCenter,
        Text(s"final score: ${model.score}", 24, centerY - 15, 1, Assets.fontKey).alignLeft,
        Text("press Enter to restart", centerX, centerY + 40, 1, Assets.fontKey).alignCenter
      )
  }

}
