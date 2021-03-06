scalacOptions ++= Seq( "-unchecked", "-deprecation" )

resolvers += Classpaths.sbtPluginReleases

resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"

// App Packaging
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.5")

// Dependency Resolution
addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.0.0")

// Code Quality
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0") // scalastyle

// addSbtPlugin("com.sksamuel.scapegoat" % "sbt-scapegoat" % "1.3.3") // scapegoat

addSbtPlugin("org.wartremover"   % "sbt-wartremover" % "2.2.1") // Wartremover

// Code formatter
addSbtPlugin("com.lucidchart"    % "sbt-scalafmt"    % "1.15")

// Copyright headers
addSbtPlugin("de.heikoseeberger" % "sbt-header"      % "4.1.0")
