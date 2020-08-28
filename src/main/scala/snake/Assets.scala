package snake

import indigo.{AssetName, AssetPath, Material, Point}
import indigo.shared.assets.AssetType.Image
import indigo.shared.datatypes.Rectangle
import indigo.shared.scenegraph.Graphic

object Assets {
  private val tileRect     = Rectangle(0, 0, Settings.textureSize, Settings.textureSize)
  private val snakeTileSet = AssetName("snake")

  val assets = Seq(Image(snakeTileSet, AssetPath(s"snake.png")))

  val wall      = crop(1, 2, 3)
  val target    = crop(6, 0, 3)
  val headUp    = crop(1, 0, 1)
  val headLeft  = crop(2, 0, 1)
  val headDown  = crop(3, 0, 1)
  val headRight = crop(4, 0, 1)
  val body      = crop(5, 0, 2)

  private def crop(x: Int, y: Int, depth: Int): Graphic =
    Graphic(tileRect, depth, Material.Textured(snakeTileSet))
      .withCrop(tileRect.moveBy(Point(x * Settings.textureSize, y * Settings.textureSize)))

  def head(direction: Direction): Graphic =
    direction match {
      case Up    => headUp
      case Left  => headLeft
      case Down  => headDown
      case Right => headRight
    }
}
