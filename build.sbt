import org.jetbrains.sbtidea.Keys._

name := "scala-macro-intellij-plugin"
organization := "org.bitlap.tools"

lazy val scala213 = "2.13.8"

scalaVersion := scala213

lazy val `intellij-plugin` = (project in file("."))
  .enablePlugins(SbtIdeaPlugin)
  .settings(
    version := (ThisBuild / version).value,
    scalaVersion := scala213,
    ThisBuild / intellijPluginName := "Scala-Macro-Tools",
    // This is a EAP version, instead of release version
    ThisBuild / intellijBuild := "221.6008.13", // https://confluence.jetbrains.com/display/IDEADEV/IDEA+2022.1+latest+builds
    ThisBuild / intellijPlatform := IntelliJPlatform.IdeaCommunity,
    Global / intellijAttachSources := true,
    Compile / javacOptions ++= "--release" :: "11" :: Nil,
    Global / scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Xfatal-warnings"),
    intellijPlugins ++= Seq("com.intellij.java", "com.intellij.java-i18n", "org.intellij.scala").map(_.toPlugin),
    Compile / unmanagedResourceDirectories += baseDirectory.value / "src" / "main" / "resources",
    Test / unmanagedResourceDirectories += baseDirectory.value / "src" / "test" / "resources",
    patchPluginXml := pluginXmlOptions { xml =>
      //      xml.version = (ThisBuild / version).value
      xml.version = "0.4.3-RC"
      xml.pluginDescription = IO.read(baseDirectory.value / "src" / "main" / "resources" / "patch" / "description.html")
      xml.changeNotes = IO.read(baseDirectory.value / "src" / "main" / "resources" / "patch" / "change.html")
    },
    publish / skip := true,
  )
