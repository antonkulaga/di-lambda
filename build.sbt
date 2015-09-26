import com.typesafe.sbt.web.pipeline.Pipeline
import playscalajs.{PlayScalaJS,ScalaJSPlay}
import playscalajs.PlayScalaJS.autoImport._
import com.typesafe.sbt.SbtNativePackager.autoImport._
import com.typesafe.sbt.gzip.Import._
import com.typesafe.sbt.web.{PathMapping, SbtWeb}
import com.typesafe.sbt.web.SbtWeb.autoImport._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import play.twirl.sbt._
import sbt.Keys._
import sbt._
import spray.revolver.RevolverPlugin._
import com.typesafe.sbt.gzip.Import.gzip

val scalaJSDevStage  = Def.taskKey[Pipeline.Stage]("Apply fastOptJS on all Scala.js projects")

def scalaJSDevTaskStage: Def.Initialize[Task[Pipeline.Stage]] = Def.task { mappings: Seq[PathMapping] =>
  mappings ++ PlayScalaJS.devFiles(Compile).value ++ PlayScalaJS.sourcemapScalaFiles(fastOptJS).value
}

  // settings for all the projects
lazy val commonSettings = Seq(
  scalaVersion := Versions.scala,
  organization := "org.openscience.school",
  resolvers += sbt.Resolver.bintrayRepo("denigma", "denigma-releases"), // for scala-js-binding
  resolvers += sbt.Resolver.bintrayRepo("rmihael", "maven"),
  resolvers += sbt.Resolver.bintrayRepo("jodersky", "maven"),
  libraryDependencies ++= Dependencies.shared.value,//++Dependencies.testing.value,
  updateOptions := updateOptions.value.withCachedResolution(true), // to speed up dependency resolution
  scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation", "-feature", "-language:implicitConversions")
)

// sbt-native-packager settings to run it as daemon
lazy val packageSettings = Seq(
  maintainer := "Anton Kulaga <antonkulaga@gmail.com>",
  packageSummary:= "di-lambda software",
  packageDescription := "lambda GUI prototype"
  // serverLoading in Debian := Upstart
)

lazy val lambda = crossProject
  .crossType(CrossType.Full)
  .in(file("app"))
  .settings(commonSettings: _*)
  .settings(
    name := "di-lambda",
    version :="Versions.lambda",
    libraryDependencies ++= Dependencies.shared.value
  )
  .jsSettings(
    jsDependencies += RuntimeDOM % "test",
    libraryDependencies ++= Dependencies.sjsLibs.value
  )
  .jsConfigure(p=>p.enablePlugins(ScalaJSPlay))
  .jvmConfigure(p=>p.enablePlugins(SbtTwirl,SbtWeb))
  .jvmSettings(Revolver.settings:_*)
  .jvmSettings(
    libraryDependencies ++=
      Dependencies.akka.value ++
        Dependencies.webjars.value ++
        Dependencies.rdf.value ++
        Dependencies.otherJVM.value,
    mainClass in Compile :=Some("org.openscience.school.lambda.Main"),
    mainClass in Revolver.reStart := Some("org.openscience.school.lambda.Main"),
    resolvers += "Bigdata releases" at "http://systap.com/maven/releases/",
    resolvers += "apache-repo-releases" at "http://repository.apache.org/content/repositories/releases/",
    resolvers += "nxparser-repo" at "http://nxparser.googlecode.com/svn/repository/",
    scalaJSDevStage := scalaJSDevTaskStage.value,
    //pipelineStages := Seq(scalaJSProd,gzip),
    (emitSourceMaps in fullOptJS) := true,
    pipelineStages in Assets := Seq(scalaJSDevStage,gzip), //for run configuration
    (fullClasspath in Runtime) += (packageBin in Assets).value //to package production deps
  )

lazy val lambdaJS  = lambda.js
lazy val lambdaJVM = lambda.jvm settings( scalaJSProjects := Seq(lambdaJS))

lazy val root = Project("root",file("."),settings = commonSettings)
  .settings(
    mainClass in Compile := (mainClass in lambdaJVM in Compile).value,
    (fullClasspath in Runtime) += (packageBin in lambdaJVM in Assets).value,
    libraryDependencies += "com.lihaoyi" % "ammonite-repl" % Versions.ammonite cross CrossVersion.full,
    initialCommands in console := """ammonite.repl.Repl.run("")""" //better console
  ) dependsOn lambdaJVM aggregate(lambdaJVM, lambdaJS)
