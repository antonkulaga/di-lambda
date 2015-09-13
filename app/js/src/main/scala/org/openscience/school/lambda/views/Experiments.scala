package org.openscience.school.lambda.views

import org.denigma.binding.views.{ItemsSetView, ItemsSeqView, BindableView}
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.raw.HTMLElement
import java.util.Date
import rx.ops._
import rx.Rx
import rx.core.Var

import scala.collection.immutable.SortedSet

object Measurement{
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

case class Measurement(sample:Sample = Sample("unknown","unknown"),diode:String = "unknown",value:Double,date:Date = new Date())
case class Sample(name:String,Description:String = "")

class MeasurementView(val elem:HTMLElement,measurement:Var[Measurement]) extends BindableView{
  val params:Map[String,Any] = Map.empty
  val sample = measurement.map(m=>m.sample.name)
  val datetime = measurement.map(m=>m.date.toString)
  val diode = measurement.map(m=>m.diode)
}

class Experiments(val elem:HTMLElement,val params:Map[String,Any] = Map.empty) extends BindableView with ItemsSetView
{
  override type Item = Var[Measurement]

  override type ItemView = MeasurementView

  override val items: Var[SortedSet[Item]] =
  Var(SortedSet(
    Var(Measurement(Sample("sample"),"diode",3004.0)),
    Var(Measurement(Sample("sample"),"diode",3030.0)),
    Var(Measurement(Sample("sample"),"diode",3020.0)),
    Var(Measurement(Sample("sample"),"diode",3010.0))
  ))
  //Var(SortedSet.empty)

  override def newItem(item: Item): MeasurementView = this.constructItemView(item){
    case (el,mp)=> new ItemView(elem,item)
  }

}
