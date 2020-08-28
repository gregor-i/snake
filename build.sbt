import org.scalajs.sbtplugin.Stage

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

compile in root := Def.taskDyn{
  val stage = (root / Compile / scalaJSStage).value
  val ret           = (root / Compile / compile).value
  val buildFrontendTask = stage match {
    case Stage.FullOpt => (root / Compile / fullOptJS)
    case Stage.FastOpt => (root / Compile / fastOptJS)
  }
  streams.value.log.info(s"integrating frontend (${stage})")
  buildFrontendTask.map{buildFrontend =>
    val outputFile    = "build/snake.js"
    Seq("cp", buildFrontend.data.toString, outputFile).!!
    ret
  }
}.value
