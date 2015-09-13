object Versions extends WebJarsVersions with ScalaJSVersions with RdfVersions with SharedVersions
{
	val scala = "2.11.7"

	val akkaHttp = "1.0"

	val ammonite = "0.4.7"

	val akkaHttpExtensions = "0.0.5"

	val scalatest = "3.0.0-M7"

	val lambda = "0.0.1" 

}

trait ScalaJSVersions {

	val jqueryFacade = "0.8"

	val semanticUIFacade = "0.0.1"

}

//versions for libs that are shared between client and server
trait SharedVersions
{
	val autowire = "0.2.5"

	val flow = "2.2.2" //"2.1.1"

	val flowNative = "2.2.2" //"2.1.1"

	val squants = "0.6.0-drugage"

	val scalaTags = "0.5.1"

	val scalaCSS = "0.3.0"

	val semanticControls = "0.0.8-M4"

	val bindingControls = "0.0.8-M4"

}

trait WebJarsVersions {

	val jquery =  "2.1.4"

	val semanticUI = "2.1.3"

}

trait RdfVersions {

	val bananaRdf = "0.8.1"

	val sesame = "2.8.3"
}
