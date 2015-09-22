package org.openscience.school.lambda.routes

import akka.http.extensions.security.LoginInfo
import akka.http.extensions.stubs._
import akka.http.scaladsl.server.{Route, Directives}
import org.openscience.school.lambda.devices.SocketTransport


class Router extends Directives {

  def routes: Route = new Head().routes ~ new Pages().routes ~ new WebSockets(SocketTransport.openChannel).routes

}
