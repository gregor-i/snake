package snake

import indigo.shared.time.Seconds

object Settings {
  val textureScale        = 3
  val textureCroppingSize = 24
  val textureSize         = textureCroppingSize * textureScale

  val scorePerTarget   = 100
  val defaultTickDelay = Seconds(0.25d)

  val viewportWidth  = textureSize * GameMap.width
  val viewportHeight = textureSize * GameMap.height
}
