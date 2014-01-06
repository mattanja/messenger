name := "messenger"

version := "1.0-SNAPSHOT"

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ("com.typesafe" %% "play-plugins-mailer" % "2.2.0"),
  ("org.apache.commons" % "commons-email" % "1.2"),
  ("org.apache.james" % "apache-mime4j" % "0.7.2"),
  ("com.google.guava" % "guava" % "14.0.1"),
  ("com.typesafe.slick" %% "slick" % "1.0.1"),
  ("com.typesafe.play" %% "play-slick" % "0.5.0.8"),
  ("org.virtuslab" %% "unicorn" % "0.4"),
  ("mysql" % "mysql-connector-java" % "5.1.21"), // MySQL for cloudbees deployment
  ("com.h2database" % "h2" % "1.3.166"),
  // Swagger API docs https://github.com/wordnik/swagger-core/tree/master/modules/swagger-play2
  // https://oss.sonatype.org/content/repositories/snapshots/com/wordnik/swagger-play2_2.10/1.3-SNAPSHOT/
  // Dependencies issues: https://github.com/wordnik/swagger-core/issues/348
  ("com.wordnik" % "swagger-play2_2.10" % "1.3.1"),
  ("com.wordnik" % "swagger-play2-utils_2.10" % "1.3.1").excludeAll(ExclusionRule(organization = "org.slf4j"))
)

play.Project.playScalaSettings
