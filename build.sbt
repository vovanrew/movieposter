name := "movieposter"
 
version := "1.0" 
      
lazy val `movieposter` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
      
scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  jdbc,
  ehcache,
  ws,
  specs2 % Test,
  guice,
  "com.pauldijou" %% "jwt-play" % "0.16.0",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.13.0-play26",
  "com.iheart" %% "ficus" % "1.3.4"
)

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )