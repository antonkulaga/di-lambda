package org.openscience.school.lambda.views

import org.denigma.binding.binders.GeneralBinder
import org.denigma.binding.views.{BindableView, ItemsSetView}
import org.denigma.controls.sockets.WebSocketSubscriber
import org.opensciencce.school.lambda.domain._
import org.openscience.school.lambda.WebSocketConnector
import org.scalajs.dom.raw.HTMLElement
import rx.core.Var
import rx.ops._

import scala.collection.immutable.SortedSet

class MeasurementView(val elem:HTMLElement,measurement:Var[Measurement]) extends BindableView
{
  val sample = measurement.map(m=>m.sample.name)
  /*val datetime = measurement.map(m=>m.date.getTime.toString)
  val diode = measurement.map(m=>m.diode)
  val value = measurement.map(m=>m.value.toString)*/
}

class Measurements(val elem:HTMLElement) extends BindableView  with ItemsSetView {

  val items: Var[SortedSet[Var[Measurement]]] = Var(SortedSet.empty[Var[Measurement]])

  override type Item = Var[Measurement]

  override type ItemView = MeasurementView

  override def newItem(item: Var[Measurement]): MeasurementView = this.constructItemView(item){
    case (el,_)=>new ItemView(el,item).withBinder(new GeneralBinder(_))
  }
}

class Experiments(val elem:HTMLElement) extends BindableView
{
  self=>

  lazy val connector: WebSocketConnector = WebSocketConnector(WebSocketSubscriber("devices","guest"))

  override lazy val injector = defaultInjector
    .register("measurements")    {
      case (el, args) => new Samples(el)
        .withBinder(  new GeneralBinder(_/*,recover = self.binders.collectFirst{   case b:GeneralBinder=>b  }*/)  )}
    .register("devices") {
      case (el, args) => new DevicesView(el,connector.devices)
        .withBinder(  new GeneralBinder(_/*,recover = self.binders.collectFirst{   case b:GeneralBinder=>b  }*/)  )
    }
    .register("devices") {
      case (el, args) => new Measurements(el)
      .withBinder(  new GeneralBinder(_/*,recover = self.binders.collectFirst{   case b:GeneralBinder=>b  }*/)  )
    }



}
