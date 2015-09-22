/*
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.scaladsl.{Sink, Flow, Source}
import com.typesafe.config.ConfigFactory
import scala.concurrent.Future
import scala.util.Try
import akka.stream.{ActorMaterializerSettings, ActorMaterializer}
import akka.stream.scaladsl._
import akka.http.scaladsl.model._
import akka.http.scaladsl.Http
implicit val system = ActorSystem("hello",ConfigFactory.defaultReference())
implicit val materializer = ActorMaterializer(
  ActorMaterializerSettings(system)
    .withInputBuffer(
      initialSize = 64,
      maxSize = 64))
// Explicitly creating and wiring up a Source, Sink and Flow

val f: Flow[Int, Int, Unit] =Flow[Int].map(_ * 2)
val s2: Source[Int, Unit] = Source(1 to 6).via(f)
val result: RunnableGraph[Unit] =  s2.to(Sink.foreach(println(_)))*/
