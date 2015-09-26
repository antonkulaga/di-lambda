package org.opensciencce.school.lambda.domain

import java.util.Date

import rx.core.Var
import scala.collection.immutable._
case class Device(name:String = "undefined",port:String)


object Measurement {
  implicit val orderingVar = new Ordering[Var[Measurement]]{
    override def compare(x: Var[Measurement], y: Var[Measurement]): Int = {
      //ordering.compare(x.now,y.now)
      1
    }
  }
  implicit val ordering = new Ordering[Measurement]{
    override def compare(x: Measurement, y: Measurement): Int = {
      x.date.compareTo(y.date) match {
        case 0=> x.sample.name.compareTo(y.sample.name)
        case other=> other
      }
      1
    }
  }
}

case class DeviceData(device:Device,channels:Seq[Double],date:Date = new Date)

object Sample{
  lazy val blank = Sample("Blank","Blank Control")
}

case class Sample(name:String,description:String = "")

case class Measurement(sample:Sample,channel:Int,value:Double,blank:Double,date:Date = new Date)
{
  def round(a:Double) = Math.round(a * 1000.0) / 1000.0

  lazy val absorbance = if(blank==0.0) "N/A" else round(-Math.log10(value/blank))
  lazy val transmittance = if(blank==0.0) "N/A" else round(value/blank * 100)
}