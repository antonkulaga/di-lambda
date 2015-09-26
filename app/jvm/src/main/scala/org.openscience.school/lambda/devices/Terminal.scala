package org.openscience.school.lambda.devices

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Terminated}
import akka.io.IO
import akka.util.ByteString
import com.github.jodersky.flow.{Serial, SerialSettings}
import org.opensciencce.school.lambda.domain._

import scala.util.{Success, Failure, Try}

/**
 *  Sample actor representing a simple terminal.
 */
class Terminal(port: String, settings: SerialSettings) extends Actor with ActorLogging with Broadcaster {

  import context._

  override def preStart() = {
    log.info(s"Requesting manager to open port: ${port}, baud: ${settings.baud}")
    IO(Serial) ! Serial.Open(port, settings)
  }

  var lastMeasurements = LambdaMessages.LastMeasurements(List.empty)
  val keepLast = 5

  def receive: Receive = opening.orElse(subscribe)


  def opening:Receive = {

    case Serial.CommandFailed(cmd, reason) =>
      log.error(s"Connection failed, stopping terminal. Reason: ${reason}")
      context stop self

    case Serial.Opened(port) =>
      log.info(s"Port ${port} is now open.")
      context become opened(sender)
      context watch sender // get notified in the event the operator crashes

  }

  def subscribe:Receive = {
    case ActorMessages.SubscribeValues(actor)=>
      users = users + (actor.path.name->actor)
      actor ! lastMeasurements

    case ActorMessages.UnsubscribeValues(actor)=>
      users = users - actor.path.name
  }

  def opened(operator: ActorRef): Receive = serial(operator).orElse(subscribe)

  def process(str:String) = {
    println(str)
    if(str.contains("Measurement: ")){
      val st = str.replace("Measurement: ","")
      Try{
        val channels = st.trim.split(' ').toList.map(s=>s.toDouble)
        DeviceData(Device("di-lambda",port),channels)
      }
      match {
        case Success(value)=>
          val vals = (value::lastMeasurements.values).take(keepLast).reverse
          lastMeasurements = lastMeasurements.copy(values = vals)
          broadcast(lastMeasurements)

        case Failure(th)=> //      println("parsing failure for last measurements")
      }
    }
  }

  def serial(operator: ActorRef):Receive = {

    case Serial.Received(bytes) =>
      val arr = bytes.toArray
      Try(new String(arr.map(_.toChar)).toCharArray.map(_.toByte)) match {
        case Success(str)=>
          val decoded = bytes.decodeString("US-ASCII").trim
          process(decoded)

        case Failure(th)=>
          log.info(s"Received strange data: " + bytes)
      }

    case Serial.Closed =>
      log.info("Operator closed normally, exiting terminal.")
      context stop self

    case Terminated(`operator`) =>
      log.error("Operator crashed unexpectedly, exiting terminal.")
      context stop self

    case ":q" =>
      operator ! Serial.Close

    case str: String =>
      operator ! Serial.Write(ByteString(str))

  }

}

object Terminal {
  def apply(port: String, settings: SerialSettings): Props =
    Props(classOf[Terminal], port, settings)
}