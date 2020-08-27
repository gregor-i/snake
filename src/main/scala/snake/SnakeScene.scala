package snake

import indigo.{EventFilters, FrameContext, GlobalEvent, Outcome, SceneUpdateFragment, SubSystem}
import indigo.scenes.{Lens, Scene, SceneName}

case class SnakeSceneModel()
case class SnakeSceneViewModel()

object SnakeScene extends Scene[Unit, Unit, Unit] {
  override type SceneModel     = SnakeSceneModel
  override type SceneViewModel = SnakeSceneViewModel

  def name: SceneName                           = SceneName("Snake")
  def modelLens: Lens[Unit, SceneModel]         = Lens(getter = _ => SnakeSceneModel(), setter = (_, _) => ())
  def viewModelLens: Lens[Unit, SceneViewModel] = Lens(getter = _ => SnakeSceneViewModel(), setter = (_, _) => ())
  def eventFilters: EventFilters                = EventFilters.Default
  def subSystems: Set[SubSystem]                = Set.empty

  override def updateModel(context: FrameContext[Unit], model: SceneModel): GlobalEvent => Outcome[SceneModel] =
    event => Outcome(SnakeSceneModel(), List(event))

  override def updateViewModel(
      context: FrameContext[Unit],
      model: SceneModel,
      viewModel: SceneViewModel
  ): GlobalEvent => Outcome[SceneViewModel] =
    event => Outcome.pure(SnakeSceneViewModel())

  override def present(context: FrameContext[Unit], model: SceneModel, viewModel: SceneViewModel): SceneUpdateFragment =
    SceneUpdateFragment.empty
}
