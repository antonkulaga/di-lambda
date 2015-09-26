package org.openscience.school.lambda.views

import org.denigma.binding.binders.{Events, GeneralBinder}
import org.denigma.binding.views.{BindableView, ItemsSeqView}
import org.opensciencce.school.lambda.domain.{Sample, Measurement, DeviceData}
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement
import rx.core.{Var, Rx}
import rx.ops._
import org.denigma.binding.extensions._


import scala.collection.immutable._
import scala.util.Random

class Toolbar(val elem:HTMLElement,
  val channels: Rx[Seq[Double]],
  val blanks:Var[Seq[Double]],
            val measurements:Var[SortedSet[Var[Measurement]]]
               ) extends BindableView
{

  protected def getBlank(blank:Seq[Double],index:Int) = if(blank.size>index) blank(index).toString else "N/A"

  def updateBlank(index:Int) = {
      blanks() = blanks.now.updated(index,channels.now(index))
  }

  val last1: rx.Rx[String] = channels.map(l=>l.head.toString)
  val last2 = channels.map(l=>l(1).toString)
  val last3 = channels.map(l=>l(2).toString)

  import org.denigma.binding.extensions._
  val addBlank1 = Var(Events.createMouseEvent())
  addBlank1.handler{ updateBlank(0)}
  val addBlank2 = Var(Events.createMouseEvent())
  addBlank2.handler{ updateBlank(1)}
  val addBlank3 = Var(Events.createMouseEvent())
  addBlank3.handler{ updateBlank(2)}

  val blank1 = blanks.map{ getBlank(_,0)}
  val blank2 = blanks.map{ getBlank(_,1)}
  val blank3 = blanks.map{ getBlank(_,2)}
  val sample = Var("")


  def addMeasurement(index:Int) = {
    //measurements() = measurements.now +m
    //println("!!!!!!!"+m)
    //println(measurements.now.toList.mkString("_"))
  }

  lazy val add1 = Var(Events.createMouseEvent())
  add1.handler
  {
    addMeasurement(0)
    val index = 0
    measurements() = measurements() + Var(Measurement(Sample(sample.now+Math.random()+"wef"),new Random().nextInt(23),channels.now(index),blanks.now(index)))

  }
  lazy val add2 = Var(Events.createMouseEvent())
  add2.handler{ addMeasurement(1)
    val index = 1
    measurements() = measurements() + Var(Measurement(Sample(sample.now+Math.random()+"wef"),new Random().nextInt(23),channels.now(index),blanks.now(index)))

  }
  lazy val add3 = Var(Events.createMouseEvent())
  add3.handler{ addMeasurement(2) }

}
