package snake

import indigo.scenes.{Scene, SceneName}
import indigo._

case class GlobalModel(
    snakeSceneModel: SnakeModel
)

case class BootData()
case class StartUpData()
case class ViewModel()

object Snake extends IndigoGame[BootData, StartUpData, GlobalModel, ViewModel] {
  private val config = GameConfig(
    viewport = GameViewport(Settings.viewportWidth, Settings.viewportHeight),
    frameRate = 30,
    clearColor = ClearColor.Black,
    magnification = 1,
    advanced = AdvancedGameConfig.default
  )

  def boot(flags: Map[String, String]): BootResult[BootData] =
    BootResult(
      config,
      BootData()
    ).withAssets(Assets.assets: _*)
      .withFonts(Assets.fontInfo)

  def scenes(bootData: BootData): NonEmptyList[Scene[StartUpData, GlobalModel, ViewModel]] =
    NonEmptyList(GreetingScene, GameScene, GameOverScene)

  def initialScene(bootData: BootData): Option[SceneName] =
    Some(GreetingScene.name)

  def setup(bootData: BootData, assetCollection: AssetCollection, dice: Dice): Startup[StartupErrors, StartUpData] =
    Startup.Success(StartUpData())

  def initialModel(startupData: StartUpData): GlobalModel =
    GlobalModel(snakeSceneModel = SnakeModel.initial)

  def initialViewModel(startupData: StartUpData, model: GlobalModel): ViewModel =
    ViewModel()

}
