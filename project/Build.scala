import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName = "messenger"
  val appVersion = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    "com.typesafe" %% "play-plugins-mailer" % "2.2.0",
    "org.apache.commons" % "commons-email" % "1.2",
    "org.apache.james" % "apache-mime4j" % "0.7.2",
    "com.google.guava" % "guava" % "14.0.1",
    //"com.typesafe.slick" %% "slick" % "1.0.0",
    "mysql" % "mysql-connector-java" % "5.1.21", // MySQL for cloudbees deployment
    anorm,
    // Swagger API docs https://github.com/wordnik/swagger-core/tree/master/modules/swagger-play2
    // https://oss.sonatype.org/content/repositories/snapshots/com/wordnik/swagger-play2_2.10/1.3-SNAPSHOT/
    // Dependencies issues: https://github.com/wordnik/swagger-core/issues/348
    ("com.wordnik" % "swagger-play2_2.10" % "1.3.1"),
    ("com.wordnik" % "swagger-play2-utils_2.10" % "1.3.1").excludeAll(ExclusionRule(organization = "org.slf4j"))
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(defaultScalaSettings:_*).settings(
    // write test reports and to console
    testOptions in Test += Tests.Argument("junitxml", "console"),

    // Sonatype for swagger
    resolvers += "Sonatype for swagger repository" at "https://oss.sonatype.org/content/repositories/snapshots/"
  )
}
