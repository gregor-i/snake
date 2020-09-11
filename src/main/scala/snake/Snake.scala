package snake

import indigo.scenes.{Scene, SceneName}
import indigo._
import org.scalajs.dom

case class GlobalModel(snakeModel: SnakeModel)

case class BootData()
case class StartUpData(tickDelay: Seconds)
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

  def setup(bootData: BootData, assetCollection: AssetCollection, dice: Dice): Startup[StartupErrors, StartUpData] = {
    val params = dom.document.location.search
      .dropWhile(_ == '?')
      .split("&")
      .flatMap {
        case s"${key}=${value}" => Some((key, value))
        case _                  => None
      }
      .toMap
    Startup.Success(
      StartUpData(
        tickDelay = params.get("tickDelay").flatMap(_.toDoubleOption).map(Seconds.apply).getOrElse(Settings.defaultTickDelay)
      )
    )
  }

  def initialModel(startupData: StartUpData): GlobalModel =
    GlobalModel(snakeModel = SnakeModel.initial)

  def initialViewModel(startupData: StartUpData, model: GlobalModel): ViewModel =
    ViewModel()

}
