import com.github.play2war.plugin._

name := "Azit"

version := "1.0"

lazy val `azit` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(cache, ws,
  "com.typesafe.play" %% "play-jdbc-api" % "2.4.3",
  "com.typesafe.play" %% "play-jdbc-evolutions" % "2.4.3",
  "mysql" % "mysql-connector-java" % "5.1.20",
  "com.typesafe.play" %% "play-specs2" % "2.4.3",
  "com.typesafe.slick" %% "slick" % "3.0.2",
  "com.typesafe.slick" %% "slick-codegen" % "3.0.2",
  "com.typesafe.play" %% "play-slick" % "1.0.1",
  "com.typesafe.play" %% "play-slick-evolutions" % "1.0.1",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "org.json4s" % "json4s-jackson_2.11" % "3.3.0",
  "org.scalatra" %% "scalatra" % "2.4.0",
  "com.enragedginger" %% "akka-quartz-scheduler" % "1.4.0-akka-2.3.x",
  "net.debasishg" %% "redisclient" % "3.0",
  "com.notnoop.apns" % "apns" % "1.0.0.Beta6",
  "org.scalaj" %% "scalaj-http" % "2.2.1",
  "com.typesafe.play" %% "play-mailer" % "3.0.1"
)

scalacOptions := Seq("-feature")

//playScalaSettings

Play2WarPlugin.play2WarSettings

Play2WarKeys.servletVersion := "3.1"

Play2WarKeys.targetName := Some("ROOT")

libraryDependencies += specs2 % Test

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
resolvers += "Play2war plugins release" at "http://repository-play-war.forge.cloudbees.com/release/"

//slick <<= slickCodeGenTask
//
//sourceGenerators in Compile <+= slickCodeGenTask
//
//lazy val slick = TaskKey[Seq[File]]("gen-tables")
//
//lazy val slickCodeGenTask = (sourceManaged, dependencyClasspath in Compile, runner in Compile, streams) map { (dir, cp, r, s) =>
//  val outputDir = (dir / "slick").getPath
//  val url = "jdbc:mysql://52.192.2.71:3306/azit"
//  val jdbcDriver = "com.mysql.jdbc.Driver"
//  val slickDriver = "slick.driver.MySQLDriver"
//  val pkg = "demo"
//  toError(r.run("slick.codegen.SourceCodeGenerator", cp.files, Array(slickDriver, jdbcDriver, url, outputDir, pkg, "root", "azittrams15"), s.log))
//  val fname = outputDir + "/demo/Tables.scala"
//  Seq(file(fname))
//}