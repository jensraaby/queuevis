enablePlugins(SbtNativePackager)
enablePlugins(JavaServerAppPackaging)

lazy val commonSettings = Seq(
  organization := "com.jensraaby",
  version := "dev",
  scalaVersion := "2.11.7",
  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    "-unchecked",
    "-Ywarn-dead-code"
  )
)

lazy val versions = new {
  val akka = "2.4.1"
  val aws = "1.10.48"
  val scalatest = "2.2.5"
  val typeSafeConfig = "1.3.0"
}

val dependencies = Seq(
  "com.amazonaws" % "aws-java-sdk-sqs" % versions.aws,
  "com.typesafe.akka" %% "akka-actor" % versions.akka,
  "com.typesafe.akka" %% "akka-testkit" % versions.akka % "test",
  "org.scalatest" %%  "scalatest"   % versions.scalatest % "test",
  "com.typesafe" % "config" % versions.typeSafeConfig
)

lazy val queueVis = (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    name := "queuevis",
    libraryDependencies ++= dependencies,
    mainClass in Compile := Some("com.jensraaby.queuevis.QueueVisMain")
  )



