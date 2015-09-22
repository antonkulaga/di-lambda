package org.openscience.school.lambda.devices

import akka.actor.{ActorRef, Props, Actor, ActorLogging}
import akka.io.IO
import com.github.jodersky.flow.{SerialSettings, Serial}

class DeviceActor  extends Actor with ActorLogging // Routes
{
  implicit val system = this.context.system //for IO to work

  val ports = List(
    "/dev/ttyUSB\\d+"
  )

  var terminals = Map.empty[String,ActorRef]

  override def preStart() = {
    val cmd = Serial.Watch()
    IO(Serial) ! cmd //watch for new devices
    log.info(s"Watching ${cmd.directory} for new devices.")
  }

  override def receive: Receive = {

    case Serial.CommandFailed(w: Serial.Watch, err) =>
      log.error(err, s"Could not get a watch on ${w.directory}.")
      context stop self

    case Serial.Connected(path) =>
      ports.find(path.matches) match {
        case Some(port) =>
          log.info(s"New device: ${path}")
          val settings: SerialSettings = SerialSettings( 115200 , 8)
          terminals = terminals + (port ->  context.actorOf(Terminal(port,settings)))
        case None => //log.warning(s"Device is NOT serial device.")
      }

    case other => println("something else received "+other)

  }

  def onStop(): Unit = {
    log.info("Device actor has been stopped...")
  }

}

object DeviceActor {
  def apply() = Props(classOf[DeviceActor])
}
