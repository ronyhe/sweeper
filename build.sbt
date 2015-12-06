name := "sweeper"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test"

resolvers += "swt-repo" at "http://maven-eclipse.github.io/maven"

libraryDependencies += {
  val os = (sys.props("os.name"), sys.props("os.arch")) match {
    case ("Linux", "amd64") => "gtk.linux.x86_64"
    case ("Linux", _) => "gtk.linux.x86"
    case ("Mac OS X", "amd64" | "x86_64") => "cocoa.macosx.x86_64"
    case ("Mac OS X", _) => "cocoa.macosx.x86"
    case (osText, "amd64") if osText.startsWith("Windows") => "win32.win32.x86_64"
    case (osText, _) if osText.startsWith("Windows") => "win32.win32.x86"
    case (osText, arch) => sys.error("Cannot obtain lib for OS '" + osText + "' and architecture '" + arch + "'")
  }
  val artifact = "org.eclipse.swt." + os
  "org.eclipse.swt" % artifact % "4.5.1"
}