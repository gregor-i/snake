package snake

import indigo.{AssetName, AssetPath, Material, Point}
import indigo.shared.assets.AssetType.Image
import indigo.shared.datatypes.Rectangle
import indigo.shared.scenegraph.Graphic

object Assets {
  private val tileRect     = Rectangle(0, 0, Settings.textureSize, Settings.textureSize)
  private val snakeTileSet = AssetName("snake")

  val assets = Seq(Image(snakeTileSet, AssetPath(s"snake.png")))

  val wall      = crop(Graphic(tileRect, 2, Material.Textured(snakeTileSet)), 7, 0)
  val target    = crop(Graphic(tileRect, 2, Material.Textured(snakeTileSet)), 0, 0)
  val headUp    = crop(Graphic(tileRect, 2, Material.Textured(snakeTileSet)), 1, 3)
  val headLeft  = crop(Graphic(tileRect, 2, Material.Textured(snakeTileSet)), 2, 3)
  val headDown  = crop(Graphic(tileRect, 2, Material.Textured(snakeTileSet)), 3, 3)
  val headRight = crop(Graphic(tileRect, 2, Material.Textured(snakeTileSet)), 4, 3)
  val body      = crop(Graphic(tileRect, 2, Material.Textured(snakeTileSet)), 5, 3)

  private def crop(graphic: Graphic, x: Int, y: Int): Graphic =
    graphic.withCrop(tileRect.moveBy(Point(x * Settings.textureSize, y * Settings.textureSize)))

  def head(direction: Direction): Graphic =
    direction match {
      case Up    => headUp
      case Left  => headLeft
      case Down  => headDown
      case Right => headRight
    }
}
