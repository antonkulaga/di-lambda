package org.openscience.school.lambda


import java.nio.ByteBuffer

import boopickle.Default._
import org.denigma.binding.extensions._
import org.denigma.controls.sockets.WebSocketSubscriber
import org.opensciencce.school.lambda.domain.LambdaMessages.Discover
import org.opensciencce.school.lambda.domain._
import org.scalajs.dom
import rx.core.Var
import scala.collection.immutable._

case class WebSocketConnector(subscriber:WebSocketSubscriber) extends LambdaPicklers with BinaryWebSocket
{
  subscriber.onOpen.handler{
    dom.console.log("IT OPENS!")
    val disc = LambdaMessages.Discover()
    send(disc)
  }

  def send(lambdaMessages: LambdaMessages.LambdaMessage) = {
    val mes = bytes2message(Pickle.intoBytes(lambdaMessages))
    subscriber.send(mes)
  }

  subscriber.onClose.handler(
    dom.alert("CLOSED")
  )
  subscriber.onMessage.onChange("OnMessage",uniqueValue = false)(onMessage)
  chosen.onChange("chosenChange")(onChosenChange)

  def discover() = {
    val bytes = Pickle.intoBytes[Discover](Discover())
    subscriber.send(bytes)
  }

  import rx.ops._
  lazy val devices:Var[Seq[Var[Device]]] = Var(Seq.empty)
  lazy val chosen:Var[Option[Device]] = Var(None)
  lazy val data:Var[Seq[DeviceData]] = Var(Seq.empty)
  lazy val channels = data.map(d=>if(d.isEmpty)Seq(0.0,0.0,0.0) else d.last.channels)

  protected def onChosenChange(opt:Option[Device]): Unit ={
    send(LambdaMessages.SelectDevice(opt))
  }

  override protected def updateFromMessage(bytes: ByteBuffer): Unit = Unpickle[LambdaMessages.LambdaMessage].fromBytes(bytes) match
  {
      case LambdaMessages.Discovered(devs,_,_)=>
        this.devices() = devs.map(Var(_))
        val d = chosen.now
        if(d.isEmpty || !devs.contains(d.get)){
          if(devs.nonEmpty) chosen() = Some(devs.head)
        }

      case LambdaMessages.LastMeasurements(vals,channel,date)=>
        //println("VALUES RECEIVED "+vals)
        this.data() = vals

        //if(chosen.now.contains())

      case other => dom.alert(s"MESSAGE RECEIVED! "+other)
  }
}
