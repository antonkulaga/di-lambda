package org.openscience.school.lambda

import org.denigma.binding.binders.GeneralBinder
import org.denigma.binding.extensions._
import org.denigma.binding.views.BindableView
import org.denigma.controls.login.{AjaxSession, LoginView}
import org.openscience.school.lambda.views.{Experiments, Devices}
import org.querki.jquery._
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement
import org.semantic.SidebarConfig
import org.semantic.ui._

import scala.collection.immutable.Map
import scala.scalajs.js.annotation.JSExport

@JSExport("FrontEnd")
object FrontEnd extends BindableView with scalajs.js.JSApp
{

  override def name: String = "main"

  lazy val elem: HTMLElement = dom.document.body

  override val params: Map[String, Any] = Map.empty
  /**
   * Register views
   */
  override lazy val injector = defaultInjector
    .register("devices"){ case (el, args) => new Devices(el, args).withBinder(new GeneralBinder(_)) }
    .register("experiments"){ case (el, args) => new Experiments(el, args).withBinder(new GeneralBinder(_)) }

  this.withBinder(new GeneralBinder(_))

  @JSExport
  def main(): Unit = {
    dom.console.log("ITWORKS!")
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
