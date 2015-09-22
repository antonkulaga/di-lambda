package org.openscience.school.lambda.devices

import java.util.Date

import akka.actor._
import akka.http.extensions.utils.BiMap
import akka.io.IO
import com.github.jodersky.flow.internal.SerialConnection
import com.github.jodersky.flow.{SerialSettings, Serial}
import org.opensciencce.school.lambda.domain.{Device, LambdaMessages}

object ActorMessages {

  case class UserJoined(user:String,userActor:ActorRef,channel:String = "devices",date:Date = new Date) extends LambdaMessages.LambdaMessage
  case class UserLeft(user:String,channel:String = "devices",date:Date = new Date) extends LambdaMessages.LambdaMessage

}
import ActorMessages._ //UGLY TRICK!

class DeviceActor()  extends Actor with ActorLogging // Routes
{
  implicit val system = this.context.system //for IO to work

  var terminals:BiMap[String,ActorRef] = BiMap.empty

  var users:Map[String,ActorRef] = Map.empty

  val ports = List(
    "/dev/ttyUSB\\d+"
  )

  override def preStart() = {
    SerialConnection.debug(true)
    val cmd = Serial.Watch()
    IO(Serial) ! cmd //watch for new devices
    log.info(s"Watching ${cmd.directory} for new devices.")
  }

  def terminalsIntoDevices() = {
    val devices:List[Device]  = (for(p<-terminals.keys) yield Device("di-lambda",p) ).toList
    LambdaMessages.Discovered(devices)
  }

  def broadcast[T](mess:T) = { for{
    (u,socket)<-users
  }{
    socket ! mess
  }
    println("broadcast to users "+users.keys.toList.mkString("=="))
  }

  override def receive: Receive = {

    case d:LambdaMessages.Discover=>
      println(s"got discover message! $d")
      broadcast(terminalsIntoDevices())

    case Serial.CommandFailed(w: Serial.Watch, err) =>
      log.error(err, s"Could not get a watch on ${w.directory}.")
      context stop self

    case Serial.Connected(path) =>
      ports.find(path.matches) match {
        case Some(port) =>
          log.info(s"New device: ${path}")
          val settings: SerialSettings = SerialSettings( 9600 , 8)
          terminals = terminals + (port ->  context.actorOf(Terminal(path,settings)))
          broadcast( terminalsIntoDevices())

        case None => //log.warning(s"Device is NOT serial device.")

      }

    case UserJoined(user,actor,channel,date)=>
      println(s"USER JOINED $user")
      this.users = users + (user->actor)
      actor ! terminalsIntoDevices()

    case UserLeft(user,channel,date)=>
      this.users = users - user

    case Terminated(ref)=>
      this.terminals.inverse.get(ref) match {
        case Some(port) =>
          terminals = terminals - port
          log.info(s"device on port $port disconnected")
        case None =>
      }

    case other => println("UNKNOWN MESSAGE"+other)

  }

  def onStop(): Unit = {
    log.info("Device actor has been stopped...")
  }

}

object DeviceActor {
  def apply() = Props(classOf[DeviceActor])
}
