package org.openscience.school.lambda


import java.nio.ByteBuffer

import boopickle.Default._
import org.denigma.binding.extensions._
import org.denigma.controls.models.WebMessage
import org.denigma.controls.sockets.WebSocketSubscriber
import org.opensciencce.school.lambda.domain.LambdaMessages.Discover
import org.opensciencce.school.lambda.domain.{Measurement, Device, LambdaMessages, LambdaPicklers}
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

  lazy val devices:Var[Seq[Var[Device]]] = Var(Seq.empty)
  lazy val chosen:Var[Option[Device]] = Var(None)

  protected def onChosenChange(opt:Option[Device]): Unit ={
    send(LambdaMessages.SelectDevice(opt))
  }


  lazy val dataFromChosen:Var[Seq[Measurement]] = Var(List.empty)


  override protected def updateFromMessage(bytes: ByteBuffer): Unit = Unpickle[WebMessage].fromBytes(bytes) match
  {
      case LambdaMessages.Discovered(devs,_,_)=>
        dom.console.log(s"Discovered RECEIVED! "+devs)
        this.devices() = devs.map(Var(_))
        //if(chosen.now.contains())

      case other => dom.alert(s"MESSAGE RECEIVED! "+other)
  }
}
