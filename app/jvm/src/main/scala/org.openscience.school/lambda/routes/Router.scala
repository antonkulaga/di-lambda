package org.openscience.school.lambda.routes

import akka.actor.ActorRef
import akka.http.scaladsl.server.{Directives, Route}
import org.openscience.school.lambda.devices.SocketTransport


class Router(deviceActor:ActorRef) extends Directives {

  def routes: Route = new Head().routes ~ new Pages().routes ~ new WebSockets(SocketTransport(deviceActor).webSocketFlow).routes

}
