name := "EditDistanceExercise"

version := "1.0"

scalaVersion := "2.11.7"

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies ++= {
  Seq(
    "org.scalatest"                 %% "scalatest"        % "2.2.4"   % "test",
    "com.assembla.scala-incubator"  %% "graph-core"       % "1.10.1"
  )
}
