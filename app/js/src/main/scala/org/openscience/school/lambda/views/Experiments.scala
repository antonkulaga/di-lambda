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





class Experiments(val elem:HTMLElement) extends BindableView
{
  self=>

  lazy val connector: WebSocketConnector = WebSocketConnector(WebSocketSubscriber("devices","guest"))

  override lazy val injector = defaultInjector
    .register("samples")    {
      case (el, args) => new Samples(el)
        .withBinder(  new GeneralBinder(_/*,recover = self.binders.collectFirst{   case b:GeneralBinder=>b  }*/)  )}
    .register("values") {
      case (el, args) => new RawDataView(el,connector.values)
        .withBinder(  new GeneralBinder(_/*,recover = self.binders.collectFirst{   case b:GeneralBinder=>b  }*/)  )
    }
    .register("devices") {
      case (el, args) => new DevicesView(el,connector.devices)
      .withBinder(  new GeneralBinder(_/*,recover = self.binders.collectFirst{   case b:GeneralBinder=>b  }*/)  )
    }
    .register("measurements") {
      case (el, args) => new Measurements(el)
        .withBinder(  new GeneralBinder(_/*,recover = self.binders.collectFirst{   case b:GeneralBinder=>b  }*/)  )
    }


}
