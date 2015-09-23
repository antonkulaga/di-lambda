package org.openscience.school.lambda.devices

import akka.actor.Actor.Receive
import akka.actor.{ActorRef, Actor}

trait Broadcaster {
  //self:Actor=>

  var users:Map[String,ActorRef] = Map.empty

  def broadcast[T](mess:T) =   for{  (u,socket)<-users }{
    //println(s"broadcast for $u")
    socket ! mess
  }

}
