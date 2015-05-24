import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "visualization"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "org.scala-tools" % "scala-stm_2.9.1" % "0.3",
      "com.jllvm" % "jllvm" % "0.0.1-SNAPSHOT"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      resolvers += "Local Maven Repository" at Path.userHome.asFile.toURI.toURL+"/.m2/repository"
    )

}
