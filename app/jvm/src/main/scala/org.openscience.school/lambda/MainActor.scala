package org.openscience.school.lambda

import akka.actor._
import akka.http.scaladsl.{Http, _}
import akka.io.IO
import akka.stream.ActorMaterializer
import com.github.jodersky.flow.Serial
import org.openscience.school.lambda.routes.Router

class MainActor extends Actor with ActorLogging // Routes
{
  implicit val system = context.system
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val server: HttpExt = Http(context.system)
  val router: Router = new Router()

  val ports = List(
    "/dev/ttyUSB\\d+"
  )

  override def preStart() = {
    val cmd = Serial.Watch()
    IO(Serial) ! cmd //watch for new devices
    log.info(s"Watching ${cmd.directory} for new devices.")
  }

  override def receive: Receive = {
    case AppMessages.Start(config)=>
      val (host,port) = (config.getString("app.host") , config.getInt("app.port"))
      log.info(s"starting server at $host:$port")
      server.bindAndHandle(router.routes, host, port)

    case AppMessages.Stop=> onStop()

    case Serial.CommandFailed(w: Serial.Watch, err) =>
      log.error(err, s"Could not get a watch on ${w.directory}.")
      context stop self

    case Serial.Connected(path) =>
      ports.find(path.matches) match {
        case Some(port) =>
          log.info(s"New device: ${path}")
        case None => //log.warning(s"Device is NOT serial device.")
      }
  }

  def onStop(): Unit = {
    log.info("Main actor has been stoped...")
  }

  override def postStop(): Unit  = {
    onStop()
  }

}
