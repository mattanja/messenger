import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName = "messenger"
  val appVersion = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    "com.typesafe" %% "play-plugins-mailer" % "2.1.0",
    "org.apache.commons" % "commons-email" % "1.3.1",
     "org.apache.james" % "apache-mime4j" % "0.7.2",            
    anorm)

  val main = play.Project(appName, appVersion, appDependencies).settings( // Add your own project settings here      
  )

}
