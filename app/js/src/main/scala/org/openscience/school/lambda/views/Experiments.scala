package org.openscience.school.lambda.views

import org.denigma.binding.binders.GeneralBinder
import org.denigma.binding.views.{BindableView, ItemsSetView}
import org.denigma.controls.sockets.WebSocketSubscriber
import org.opensciencce.school.lambda.domain._
import org.openscience.school.lambda.WebSocketConnector
import org.scalajs.dom.raw.HTMLElement
import rx.core.Var
import rx.ops._

import scala.collection.immutable._


class Experiments(val elem:HTMLElement) extends BindableView
{
  self=>

  lazy val connector: WebSocketConnector = WebSocketConnector(WebSocketSubscriber("devices","guest"))

  val blanks: Var[Seq[Double]] = Var(Seq[Double](0.0,0.0,0.0))
  val samples: Var[Seq[Var[Sample]]] = Var(Seq.empty)

  override lazy val injector = defaultInjector
    .register("samples")    {
      case (el, args) => new SamplesView(el,samples)
        .withBinder(  new GeneralBinder(_)  )}
    .register("data") {
      case (el, args) => new RawDataView(el,connector.values,blanks)
        .withBinder(  new GeneralBinder(_)  )
    }
    .register("devices") {
      case (el, args) => new DevicesView(el,connector.devices)
      .withBinder(  new GeneralBinder(_)  )
    }
    .register("measurements") {
      case (el, args) => new Measurements(el,blanks)
        .withBinder(  new GeneralBinder(_)  )
    }

}
