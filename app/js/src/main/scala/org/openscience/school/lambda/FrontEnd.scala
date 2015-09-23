package org.openscience.school.lambda

import org.denigma.binding.binders.GeneralBinder
import org.denigma.binding.extensions._
import org.denigma.binding.views.BindableView
import org.denigma.controls.sockets.WebSocketSubscriber
import org.openscience.school.lambda.views._
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement

import scala.scalajs.js.annotation.JSExport

@JSExport("FrontEnd")
object FrontEnd extends BindableView with scalajs.js.JSApp
{

  override def name: String = "main"

  lazy val elem: HTMLElement = dom.document.body

  lazy val connector: WebSocketConnector = WebSocketConnector(WebSocketSubscriber("devices","guest"))
  //println(connector.subscriber.urlOpt.now)
  //val devices = connector.devices


  /**
   * Register views
   */
  override lazy val injector = defaultInjector
    .register("devices"){ case (el, args) => new DevicesView(el,connector.devices).withBinder(new GeneralBinder(_)) }
    .register("data"){  case (el,args)=> new DataView(el,connector.values).withBinder(new GeneralBinder(_))   }
    .register("samples"){  case (el,args)=> new Samples(el).withBinder(new GeneralBinder(_))   }

  //.register("experiments"){ case (el, args) => new Experiments(el).withBinder(new GeneralBinder(_)) }

  this.withBinder(new GeneralBinder(_))

  @JSExport
  def main(): Unit = {
    this.bindElement(this.viewElement)
  }


  @JSExport
  def load(content: String, into: String): Unit = {
    dom.document.getElementById(into).innerHTML = content
  }

  @JSExport
  def moveInto(from: String, into: String): Unit = {
    for {
      ins <- sq.byId(from)
      intoElement <- sq.byId(into)
    } {
      this.loadElementInto(intoElement, ins.innerHTML)
      ins.parentNode.removeChild(ins)
    }
  }
}
