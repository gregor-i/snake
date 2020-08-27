package snake

import indigo.scenes.{Scene, SceneName}
import indigo._

case class GameModel(
    snakeSceneModel: SnakeSceneModel
)

case class BootData()
case class StartUpData()
case class ViewModel()

object Snake extends IndigoGame[BootData, StartUpData, GameModel, ViewModel] {
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

  def scenes(bootData: BootData): NonEmptyList[Scene[StartUpData, GameModel, ViewModel]] =
    NonEmptyList(SnakeScene)

  def initialScene(bootData: BootData): Option[SceneName] =
    Some(SceneName("Snake"))

  def setup(bootData: BootData, assetCollection: AssetCollection, dice: Dice): Startup[StartupErrors, StartUpData] =
    Startup.Success(StartUpData())

  def initialModel(startupData: StartUpData): GameModel =
    GameModel(snakeSceneModel = SnakeSceneModel())

  def initialViewModel(startupData: StartUpData, model: GameModel): ViewModel =
    ViewModel()

}
