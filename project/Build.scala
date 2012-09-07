import sbt._
import Keys._


object MinimalBuild extends Build {
  val SNAPSHOT = "-SNAPSHOT"

  lazy val buildVersion =  "0.0.1" + SNAPSHOT

  /* LOCAL MAVEN REPO */
  val localMavenRepo = "Local Maven Repository" at file(Path.userHome.absolutePath+"/.m2/repository").toURI.toURL.toString

  lazy val typesafeSnapshot = "Typesafe Snapshots Repository" at "http://repo.typesafe.com/typesafe/snapshots/"
  lazy val typesafe = "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

  lazy val mongoDBEmbed  = "de.flapdoodle.embed" % "de.flapdoodle.embed.mongo" % "1.23"


  val cloudbees = "https://repository-andy-petrella.forge.cloudbees.com/"
  val cloudbeesRepo = buildVersion match {
    case x if x.endsWith(SNAPSHOT) => x.toLowerCase at cloudbees + "snapshot" + "/"
    case x => x.toLowerCase at cloudbees + "release" + "/"
  }

  val libDependencies = Seq(
    "play" %% "play" % "2.0",

    mongoDBEmbed,

    "play" %% "play-test" % "2.0" % "test"
  )


  val cloudbeesCredentials = Credentials(file("project/cloudbees.credentials"))
  lazy val root = {
    Project(id = "play-plugins-embed-mongodb", base = file("."), settings = Project.defaultSettings).settings(
      version := buildVersion,
      organization := "be.nextlab",
      resolvers ++= Seq(typesafe, typesafeSnapshot, localMavenRepo, cloudbeesRepo),
      javacOptions += "-Xlint:unchecked",
      libraryDependencies ++= libDependencies,
      publishMavenStyle := true,
      publishTo := Some(cloudbeesRepo),
      credentials += cloudbeesCredentials
    )
  }
}