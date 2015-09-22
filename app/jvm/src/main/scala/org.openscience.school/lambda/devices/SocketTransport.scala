package org.openscience.school.lambda.devices

import akka.http.scaladsl.model.ws._
import akka.stream.scaladsl.Flow
import akka.stream.stage.{Context, PushStage, SyncDirective, TerminationDirective}
import akka.util.ByteString
import boopickle.Default._
import org.denigma.controls.models.{Suggest, Suggestion, WebMessage, WebPicklers}
import org.opensciencce.school.lambda.domain.LambdaMessages
import org.opensciencce.school.lambda.domain.LambdaMessages.Discovered

object SocketTransport extends WebPicklers {

  def openChannel(channel: String, username: String = "guest"): Flow[Message, Message, Unit] = (channel, username) match {
    case (_, _) =>
      Flow[Message].collect {
        case BinaryMessage.Strict(data) =>
          println("IT IS ALIVE!")
          Unpickle[WebMessage].fromBytes(data.toByteBuffer) match {
            case LambdaMessages.Discover(_,_)=>
              val disc = Discovered(List.empty)
              val d = Pickle.intoBytes[WebMessage](disc)
              BinaryMessage(ByteString(d))
          }
      }.via(reportErrorsFlow(channel,username)) // ... then log any processing errors on stdin
  }


  def reportErrorsFlow[T](channel:String,username:String): Flow[T, T, Unit] =
    Flow[T]
      .transform(() ⇒ new PushStage[T, T] {
        def onPush(elem: T, ctx: Context[T]): SyncDirective = ctx.push(elem)

        override def onUpstreamFailure(cause: Throwable, ctx: Context[T]): TerminationDirective = {
          println(s"WS stream for $channel failed for $username with the following cause:\n  $cause")
          super.onUpstreamFailure(cause, ctx)
        }
      })

}
