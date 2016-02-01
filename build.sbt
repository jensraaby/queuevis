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
  val elasticmq = "0.8.12"
}

val dependencies = Seq(
  "com.typesafe.akka" %% "akka-actor" % versions.akka,
  "com.amazonaws" % "aws-java-sdk-sqs" % versions.aws,
  "org.elasticmq" %% "elasticmq-rest-sqs" % versions.elasticmq
)

lazy val queueVis = (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    name := "queuevis",
    libraryDependencies ++= dependencies,
    mainClass in Compile := Some("com.jensraaby.queuevis.QueueVisMain")

  )



