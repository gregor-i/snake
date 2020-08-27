package snake

import indigo._
import indigo.scenes.{Lens, Scene, SceneName}
import org.scalajs.dom

import scala.scalajs.js

object Main extends IndigoGame[Unit, Unit, Unit, Unit] {
  private val config = GameConfig(
    viewport = GameViewport(1024, 800),
    frameRate = 30,
    clearColor = ClearColor.White,
    magnification = 1,
    advanced = AdvancedGameConfig.default
  )

  override def boot(flags: Map[String, String]): BootResult[Unit] =
    BootResult(
      config,
      ()
    )
  override def scenes(bootData: Unit): NonEmptyList[Scene[Unit, Unit, Unit]]                                     = NonEmptyList(SnakeScene)
  override def initialScene(bootData: Unit): Option[SceneName]                                                   = Some(SceneName("Snake"))
  override def setup(bootData: Unit, assetCollection: AssetCollection, dice: Dice): Startup[StartupErrors, Unit] = Startup.Success(())
  override def initialModel(startupData: Unit): Unit                                                             = ()
  override def initialViewModel(startupData: Unit, model: Unit): Unit                                            = ()

  def main(args: Array[String]): Unit = {
    dom.document.addEventListener[dom.Event](
      "DOMContentLoaded",
      (_: js.Any) => this.launch()
    )
  }
}
