import sbt.Keys._
import de.heikoseeberger.sbtheader.license.Apache2_0

headers := Map(
  "scala" -> Apache2_0("2015", "Depop"),
  "conf" -> Apache2_0("2015", "Depop", "#")
)

val commonSettings: Seq[Def.Setting[_]] = Seq(
  organization := "depop",
  version := "1.0",
  scalaVersion := "2.11.7"
)

lazy val dataModel = project.in(file("model"))
  .settings(commonSettings: _*)
  .settings(
    name := "data-model",
    libraryDependencies ++= Seq(
      "io.spray" %% "spray-json" % "1.3.2"
    )
  )

lazy val macroProject = project.in(file("."))
  .enablePlugins(AutomateHeaderPlugin)
  .dependsOn(dataModel)
  .settings(commonSettings: _*)
  .settings(
    name := "json-macros",
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      "io.spray" %% "spray-json" % "1.3.2" % "provided",
      "org.specs2" %% "specs2-core" % "3.6.5" % "test",
      "org.specs2" %% "specs2-mock" % "3.6.5" % "test"
    )
  )

