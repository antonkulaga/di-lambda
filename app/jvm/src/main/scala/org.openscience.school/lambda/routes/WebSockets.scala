package org.openscience.school.lambda.routes

import akka.http.scaladsl.server._
import akka.http.extensions.security._
import akka.http.scaladsl.model.Uri.Path
import akka.http.scaladsl.model.ws.{UpgradeToWebsocket, Message}
import akka.http.scaladsl.server._
import akka.stream.scaladsl.{Source, Sink, Flow}


class WebSockets(
                  makeChannel: (String,String) => Flow[Message, Message, _]
                  ) extends  AuthDirectives with Directives with WithLoginRejections with WithRegistrationRejections
{

  def routes: Route =
    pathPrefix("connect") {
      parameters("channel","username"){
        case (channel,username)=>
          handleWebsocketMessages(makeChannel(channel,username))
      }
    }
}
