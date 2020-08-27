import scala.sys.process._

scalaVersion in ThisBuild  := "2.13.3"
scalafmtOnCompile in ThisBuild := true

val root = project.in(file("."))
  .enablePlugins(ScalaJSPlugin, SbtIndigo)
  .settings(scalaJSUseMainModuleInitializer := true)
  .settings(
    libraryDependencies ++= Seq(
    "io.indigoengine" %%% "indigo"            % "0.3.0",
    "io.indigoengine" %%% "indigo-extras"     % "0.3.0"
    )
  )

compile in root := {
  val ret           = (root / Compile / compile).value
  val buildFrontend = (root / Compile / fastOptJS).value.data
  val outputFile    = "build/snake.js"
  streams.value.log.info("integrating frontend (fastOptJS)")
  val npmLog = Seq("cp", buildFrontend.toString, outputFile).!!
  streams.value.log.info(npmLog)
  ret
}