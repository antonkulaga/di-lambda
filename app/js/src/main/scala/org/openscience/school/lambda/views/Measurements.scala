package org.openscience.school.lambda.views
import org.denigma.binding.binders.GeneralBinder
import org.denigma.binding.views.{BindableView, ItemsSetView}
import org.opensciencce.school.lambda.domain._
import org.scalajs.dom.raw.HTMLElement
import rx.core.Var
import rx.ops._

import scala.collection.immutable.SortedSet

class Measurements(val elem:HTMLElement) extends BindableView  with ItemsSetView {

  val items: Var[SortedSet[Var[Measurement]]] = Var(SortedSet.empty[Var[Measurement]])

  override type Item = Var[Measurement]

  override type ItemView = MeasurementView

  override def newItem(item: Var[Measurement]): MeasurementView = this.constructItemView(item){
    case (el,_)=>new ItemView(el,item).withBinder(new GeneralBinder(_))
  }
}

/**
 * Created by antonkulaga on 9/26/15.
 */
class MeasurementView(val elem:HTMLElement,measurement:Var[Measurement]) extends BindableView
{
  val sample = measurement.map(m=>m.sample.name)
  /*val datetime = measurement.map(m=>m.date.getTime.toString)
  val diode = measurement.map(m=>m.diode)
  val value = measurement.map(m=>m.value.toString)*/
}
