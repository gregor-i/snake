package snake

import indigo._
import indigo.shared.assets.AssetType.Image
import indigo.shared.datatypes.Rectangle
import indigo.shared.scenegraph.Graphic

object Assets {
  private val tileRect     = Rectangle(0, 0, Settings.textureCroppingSize, Settings.textureCroppingSize)
  private val snakeTileSet = AssetName("snake")
  private val fontTileSet  = AssetName("font")
  val fontKey              = FontKey("font")

  val assets = Seq(
    AssetType.Image(snakeTileSet, AssetPath("snake.png")),
    AssetType.Image(fontTileSet, AssetPath("boxy_font_small.png"))
  )

  val wall      = crop(1, 2, 3)
  val target    = crop(6, 0, 3)
  val headUp    = crop(1, 0, 1)
  val headLeft  = crop(2, 0, 1)
  val headDown  = crop(3, 0, 1)
  val headRight = crop(4, 0, 1)
  val body      = crop(5, 0, 2)

  private def crop(x: Int, y: Int, depth: Int): Graphic =
    Graphic(tileRect, depth, Material.Textured(snakeTileSet))
      .withCrop(tileRect.moveBy(Point(x, y) * Settings.textureCroppingSize))
      .scaleBy(Settings.textureScale, Settings.textureScale)

  def head(direction: Direction): Graphic =
    direction match {
      case Up    => headUp
      case Left  => headLeft
      case Down  => headDown
      case Right => headRight
    }

  val fontInfo: FontInfo =
    FontInfo(fontKey, Material.Textured(fontTileSet), 320, 230, FontChar("?", 47, 26, 11, 12))
      .addChar(FontChar("A", 2, 39, 10, 12))
      .addChar(FontChar("B", 14, 39, 9, 12))
      .addChar(FontChar("C", 25, 39, 10, 12))
      .addChar(FontChar("D", 37, 39, 9, 12))
      .addChar(FontChar("E", 49, 39, 9, 12))
      .addChar(FontChar("F", 60, 39, 9, 12))
      .addChar(FontChar("G", 72, 39, 9, 12))
      .addChar(FontChar("H", 83, 39, 9, 12))
      .addChar(FontChar("I", 95, 39, 5, 12))
      .addChar(FontChar("J", 102, 39, 9, 12))
      .addChar(FontChar("K", 113, 39, 10, 12))
      .addChar(FontChar("L", 125, 39, 9, 12))
      .addChar(FontChar("M", 136, 39, 13, 12))
      .addChar(FontChar("N", 2, 52, 11, 12))
      .addChar(FontChar("O", 15, 52, 10, 12))
      .addChar(FontChar("P", 27, 52, 9, 12))
      .addChar(FontChar("Q", 38, 52, 11, 12))
      .addChar(FontChar("R", 51, 52, 10, 12))
      .addChar(FontChar("S", 63, 52, 9, 12))
      .addChar(FontChar("T", 74, 52, 11, 12))
      .addChar(FontChar("U", 87, 52, 10, 12))
      .addChar(FontChar("V", 99, 52, 9, 12))
      .addChar(FontChar("W", 110, 52, 13, 12))
      .addChar(FontChar("X", 125, 52, 9, 12))
      .addChar(FontChar("Y", 136, 52, 11, 12))
      .addChar(FontChar("Z", 149, 52, 10, 12))
      .addChar(FontChar("0", 2, 13, 10, 12))
      .addChar(FontChar("1", 13, 13, 7, 12))
      .addChar(FontChar("2", 21, 13, 9, 12))
      .addChar(FontChar("3", 33, 13, 9, 12))
      .addChar(FontChar("4", 44, 13, 9, 12))
      .addChar(FontChar("5", 56, 13, 9, 12))
      .addChar(FontChar("6", 67, 13, 9, 12))
      .addChar(FontChar("7", 79, 13, 9, 12))
      .addChar(FontChar("8", 90, 13, 10, 12))
      .addChar(FontChar("9", 102, 13, 9, 12))
      .addChar(FontChar("?", 47, 26, 11, 12))
      .addChar(FontChar("!", 2, 0, 6, 12))
      .addChar(FontChar(".", 143, 0, 6, 12))
      .addChar(FontChar(",", 124, 0, 8, 12))
      .addChar(FontChar("-", 133, 0, 9, 12))
      .addChar(FontChar(" ", 112, 13, 12, 12))
      .addChar(FontChar("[", 2, 65, 7, 12))
      .addChar(FontChar("]", 21, 65, 7, 12))
      .addChar(FontChar("(", 84, 0, 7, 12))
      .addChar(FontChar(")", 93, 0, 7, 12))
      .addChar(FontChar("\\", 11, 65, 8, 12))
      .addChar(FontChar("/", 150, 0, 9, 12))
      .addChar(FontChar(":", 2, 26, 5, 12))
      .addChar(FontChar("_", 42, 65, 9, 12))
}
