package org.opensciencce.school.lambda.domain

import java.util.Date
import boopickle.Default._
import org.denigma.controls.models.WebMessage

object LambdaMessages {


  type LambdaMessage = WebMessage
  //trait LambdaMessage extends WebMessage

  case class Discover(channel:String = "devices",date:Date = new Date) extends LambdaMessage
  case class Discovered(devices:List[Device],channel:String = "devices",date:Date = new Date) extends LambdaMessage
  case class SelectDevice(device:Option[Device], channel:String = "devices",date:Date = new Date) extends LambdaMessage
  case class LastMeasurements(measurements:List[Measurement],channel:String = "devices",date:Date = new Date) extends LambdaMessage
}

class LambdaPicklers {

  import LambdaMessages._

  implicit val datePickler = transformPickler[java.util.Date, Long](_.getTime, t => new java.util.Date(t))

  //implicit val datePickler = generatePickler[Date]
  implicit val devicePickler = generatePickler[Device]
  implicit val samplePickler = generatePickler[Sample]
  implicit val measurementPickler = generatePickler[Measurement]


  implicit val messagesPickler = compositePickler[LambdaMessages.LambdaMessage]
    .addConcreteType[Discover]
    .addConcreteType[Discovered]
    .addConcreteType[SelectDevice]
    .addConcreteType[LastMeasurements]

}
