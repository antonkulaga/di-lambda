package org.openscience.school.lambda.views

import org.denigma.binding.binders.GeneralBinder
import org.denigma.binding.views.{BindableView, ItemsSetView}
import org.opensciencce.school.lambda.domain.{Measurement, Sample}
import org.scalajs.dom.raw.HTMLElement
import rx.core.Var
import rx.ops._

import scala.collection.immutable.SortedSet

class MeasurementView(val elem:HTMLElement,measurement:Var[Measurement]) extends BindableView
{
  val sample = measurement.map(m=>m.sample.name)
  val datetime = measurement.map(m=>m.date.getTime.toString)
  val diode = measurement.map(m=>m.diode)
  val value = measurement.map(m=>m.value.toString)
}

class Experiments(val elem:HTMLElement) extends BindableView with ItemsSetView
{
  override type Item = Var[Measurement]

  override type ItemView = MeasurementView

  override val items: Var[SortedSet[Item]] =
    Var(SortedSet(
      Var(Measurement(Sample("sample1"),"diode1",3004.0)),
      Var(Measurement(Sample("sample2"),"diode2",3030.0)),
      Var(Measurement(Sample("sample3"),"diode3",3020.0)),
      Var(Measurement(Sample("sample4"),"diode4",3010.0))
    ))
  println(SortedSet(
    Var(Measurement(Sample("sample1"),"diode1",3004.0)),
    Var(Measurement(Sample("sample2"),"diode2",3030.0)),
    Var(Measurement(Sample("sample3"),"diode3",3020.0)),
    Var(Measurement(Sample("sample4"),"diode4",3010.0))
  ).size)
  //Var(SortedSet.empty)

  override def newItem(item: Item): MeasurementView = this.constructItemView(item){
    case (el,mp)=> new ItemView(el,item).withBinder(new GeneralBinder(_))
  }

}