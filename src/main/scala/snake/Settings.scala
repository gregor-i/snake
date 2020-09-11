package snake

import indigo.shared.time.Seconds

object Settings {
  val textureScale        = 3
  val textureCroppingSize = 24
  val textureSize         = textureCroppingSize * textureScale

  val scorePerTarget   = 100
  val defaultTickDelay = Seconds(0.25d)
  val defaultWidth     = 12
  val defaultHeight    = 10

  def viewportWidth(startUpData: StartUpData)  = textureSize * startUpData.width
  def viewportHeight(startUpData: StartUpData) = textureSize * startUpData.height
}
