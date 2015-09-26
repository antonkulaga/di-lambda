package org.openscience.school.lambda.views

import org.denigma.binding.binders.GeneralBinder
import org.denigma.binding.views.{BindableView, ItemsSetView}
import org.denigma.controls.sockets.WebSocketSubscriber
import org.opensciencce.school.lambda.domain._
import org.openscience.school.lambda.WebSocketConnector
import org.scalajs.dom.raw.HTMLElement
import rx.Rx
import rx.core.Var
import rx.ops._

import scala.collection.immutable._


class Experiments(val elem:HTMLElement) extends BindableView
{
  self=>

  lazy val connector: WebSocketConnector = WebSocketConnector(WebSocketSubscriber("devices","guest"))

  lazy val blanks: Var[Seq[Double]] = Var(Seq[Double](0.0,0.0,0.0))
  lazy val samples: Var[Seq[Var[Sample]]] = Var(Seq.empty)

  val measurements = Var(SortedSet.empty[Var[Measurement]])

   measurements() = SortedSet(Var(Measurement(Sample("one"),10,12,12)),Var(Measurement(Sample("on12e"),1120,12,12)),Var(Measurement(Sample("one"),10,1212,12)))


  override lazy val injector = defaultInjector.register("measurements") {
      case (el, args) => new Measurements(el,measurements,blanks).withBinder(  new GeneralBinder(_)  )
    }
    .register("toolbar") {
      case (el, args) => new Toolbar(el,connector.channels,blanks,measurements)
        .withBinder(  new GeneralBinder(_)  )
    }
/*    .register("samples")    {
      case (el, args) => new SamplesView(el,samples)
        .withBinder(  new GeneralBinder(_)  )}
    .register("data") {
      case (el, args) => new RawDataView(el,connector.data,blanks)
        .withBinder(  new GeneralBinder(_)  )
    }
    .register("devices") {
      case (el, args) => new DevicesView(el,connector.devices)
      .withBinder(  new GeneralBinder(_)  )
    }*/


}
