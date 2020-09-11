package snake

import indigo.scenes.{Scene, SceneName}
import indigo._
import org.scalajs.dom

case class GlobalModel(snakeModel: SnakeModel)

case class BootData(startUpData: StartUpData)
case class StartUpData(tickDelay: Seconds, width: Int, height: Int)
case class ViewModel()

object Snake extends IndigoGame[BootData, StartUpData, GlobalModel, ViewModel] {

  def boot(flags: Map[String, String]): BootResult[BootData] = {
    val params = dom.document.location.search
      .dropWhile(_ == '?')
      .split("&")
      .flatMap {
        case s"${key}=${value}" => Some((key, value))
        case _                  => None
      }
      .toMap

    val startupData = StartUpData(
      tickDelay = params.get("tickDelay").flatMap(_.toDoubleOption).map(Seconds.apply).getOrElse(Settings.defaultTickDelay),
      width = params.get("width").flatMap(_.toIntOption).getOrElse(Settings.defaultWidth),
      height = params.get("height").flatMap(_.toIntOption).getOrElse(Settings.defaultHeight)
    )

    val config = GameConfig(
      viewport = GameViewport(Settings.textureSize * startupData.width, Settings.textureSize * startupData.height),
      frameRate = 30,
      clearColor = ClearColor.Black,
      magnification = 1,
      advanced = AdvancedGameConfig.default
    )

    BootResult(
      config,
      BootData(startupData)
    ).withAssets(Assets.assets: _*)
      .withFonts(Assets.fontInfo)
  }

  def scenes(bootData: BootData): NonEmptyList[Scene[StartUpData, GlobalModel, ViewModel]] =
    NonEmptyList(GreetingScene, GameScene, GameOverScene)

  def initialScene(bootData: BootData): Option[SceneName] =
    Some(GreetingScene.name)

  def setup(bootData: BootData, assetCollection: AssetCollection, dice: Dice): Startup[StartupErrors, StartUpData] =
    Startup.Success(bootData.startUpData)

  def initialModel(startupData: StartUpData): GlobalModel =
    GlobalModel(snakeModel = SnakeModel.initial)

  def initialViewModel(startupData: StartUpData, model: GlobalModel): ViewModel =
    ViewModel()

}
