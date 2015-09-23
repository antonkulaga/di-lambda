package org.opensciencce.school.lambda.domain

import java.util.Date

import rx.core.Var

case class Device(name:String = "undefined",port:String)

/*
object Measurement
{
  implicit val ordering = new Ordering[Measurement]{
    override def compare(x: Measurement, y: Measurement): Int = {
      x.date.compareTo(y.date)
    }
  }

  implicit val varOrdering = new Ordering[Var[Measurement]]{
    override def compare(x: Var[Measurement], y: Var[Measurement]): Int = {
      x.now.date.compareTo(y.now.date)
    }
  }
}
*/

case class Value(device:Device,channels:Seq[Double],date:Date = new Date)

//case class Measurement(sample:Sample,values:List[Value])
//case class Measurement(sample:Sample = Sample("unknown","unknown"),diode:String = "unknown",value:Double,date:Date = new Date())

object Sample{
  lazy val blank = Sample("Blank","Blank Control")
}
case class Sample(name:String,description:String = "")