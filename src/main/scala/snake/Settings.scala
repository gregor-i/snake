package snake

import indigo.shared.time.Seconds

object Settings {
  val textureScale        = 3
  val textureCroppingSize = 8
  val textureSize         = textureCroppingSize * textureScale

  val scorePerTarget = 100
  val tickDelay      = Seconds(0.15d)

  val viewportWidth  = textureSize * GameMap.width
  val viewportHeight = textureSize * GameMap.height
}
