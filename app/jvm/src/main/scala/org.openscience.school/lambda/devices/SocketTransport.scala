package org.openscience.school.lambda.devices

import akka.actor.ActorRef
import akka.http.scaladsl.model.ws._
import akka.stream.OverflowStrategy
import akka.stream.scaladsl._
import akka.stream.stage._
import akka.util.ByteString
import boopickle.Default._
import org.denigma.controls.models.{Suggestion, Suggest, WebMessage, WebPicklers}
import org.opensciencce.school.lambda.domain.LambdaMessages
import org.opensciencce.school.lambda.domain.LambdaMessages.{UserJoined, Discovered}
import akka.stream.scaladsl.FlowGraph.Implicits._

case class SocketTransport(deviceActor:ActorRef) extends WebPicklers {


  def webSocketFlow(channel: String, user: String = "guest"): Flow[Message, Message, _] =
  //Factory method allows for materialization of this Source
    Flow(Source.actorRef[WebMessage](bufferSize = 5, OverflowStrategy.fail)) {
      implicit builder =>
        chatSource => //it's Source from parameter

          //flow used as input, it takes Messages
          val fromWebsocket = builder.add(
            Flow[Message].collect {
              case BinaryMessage.Strict(data) =>
                println("WE GOT THE MESSAGE INSIDE!!!!!!!!!!!!!!!")
                Unpickle[LambdaMessages.LambdaMessage].fromBytes(data.toByteBuffer)
            })

          //flow used as output, it returns Messages
          val backToWebsocket = builder.add(
            Flow[LambdaMessages.LambdaMessage].map {
              case mess:LambdaMessages.LambdaMessage=>
                println("WE GOT THE MESSAGE BACK!!!!!!!!!!!!!!!"+mess)
                val d = Pickle.intoBytes[LambdaMessages.LambdaMessage](mess)
                BinaryMessage(ByteString(d))
            }
          )

          //send messages to the actor, if sent also UserLeft(user) before stream completes.
          val mainActorSink = Sink.actorRef[LambdaMessages.LambdaMessage](deviceActor,LambdaMessages.UserLeft)

          //merges both pipes
          val merge = builder.add(Merge[LambdaMessages.LambdaMessage](2))

          val actorAsSource = builder.materializedValue.map(actor => UserJoined(user))

          //Message from websocket is converted into IncommingMessage and should be sent to everyone in the room
          fromWebsocket ~> merge.in(0)

          //If Source actor is just created, it should be sent as UserJoined
          actorAsSource ~> merge.in(1)

          //Merges both pipes above and forwards messages to chatroom represented by ChatRoomActor
          merge ~> mainActorSink

          //Actor already sits in chatRoom so each message from room is used as source and pushed back into the websocket
          chatSource ~> backToWebsocket

          // expose ports
          (fromWebsocket.inlet, backToWebsocket.outlet)
    }


/*
  def openChannel(channel: String, username: String = "guest"): (Sink[Message,_],Source[Message, _]) = (channel, username) match {
    case (_, _) =>

      Source[Message].from
  /*    Flow[Message].collect {
        case BinaryMessage.Strict(data) =>
          println("IT IS ALIVE!")
          Unpickle[WebMessage].fromBytes(data.toByteBuffer) match {
            case LambdaMessages.Discover(_,_)=>
              val disc = Discovered(List.empty)
              val d = Pickle.intoBytes[WebMessage](disc)
              BinaryMessage(ByteString(d))
          }
      }.via(reportErrorsFlow(channel,username)) // ... then log any processing errors on stdin
  */
  }*/

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
