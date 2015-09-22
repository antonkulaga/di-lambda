package org.openscience.school.lambda

import akka.actor._
import akka.http.scaladsl.{Http, _}
import akka.stream.ActorMaterializer
import org.openscience.school.lambda.routes.Router

class MainActor extends Actor with ActorLogging // Routes
{
  implicit val system = context.system
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val server: HttpExt = Http(context.system)
  val router: Router = new Router()

  override def receive: Receive = {
    case AppMessages.Start(config)=>
      val (host,port) = (config.getString("app.host") , config.getInt("app.port"))
      log.info(s"starting server at $host:$port")
      server.bindAndHandle(router.routes, host, port)

    case AppMessages.Stop=> onStop()
  }

  def onStop(): Unit = {
    log.info("Main actor has been stopped...")
  }

  override def postStop(): Unit  = {
    onStop()
  }

}
