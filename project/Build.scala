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
    "com.google.guava" % "guava" % "14.0.1",
    //"com.typesafe.slick" %% "slick" % "1.0.0",
    "mysql" % "mysql-connector-java" % "5.1.21", // MySQL for cloudbees deployment
    anorm)

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // write test reports and to console
    testOptions in Test += Tests.Argument("junitxml", "console")
  )
}
