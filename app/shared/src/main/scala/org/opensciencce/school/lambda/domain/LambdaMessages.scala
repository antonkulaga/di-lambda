package org.opensciencce.school.lambda.domain

import java.util.Date

import boopickle.Default._
import org.denigma.controls.models.WebMessage

object LambdaMessages {

  case class Discover(channel:String = "devices",date:Date = new Date) extends WebMessage
  case class Discovered(devices:List[Device],channel:String = "devices",date:Date = new Date) extends WebMessage
  case class SelectDevices(devices:List[Device], channel:String = "devices",date:Date = new Date) extends WebMessage
}

class LambdaPicklers {

  import LambdaMessages._

  implicit val datePickler = transformPickler[java.util.Date, Long](_.getTime, t => new java.util.Date(t))

  //implicit val datePickler = generatePickler[Date]
  implicit val devicePickler = generatePickler[Device]
  implicit val samplePickler = generatePickler[Sample]
  implicit val measurementPickler = generatePickler[Measurement]


  implicit val messagesPickler = compositePickler[WebMessage]
    .addConcreteType[Discover]
    .addConcreteType[Discovered]
    .addConcreteType[SelectDevices]
}
