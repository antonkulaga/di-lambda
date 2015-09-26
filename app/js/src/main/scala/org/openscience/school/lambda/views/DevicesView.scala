package org.openscience.school.lambda.views


import org.denigma.binding.binders.{Events, GeneralBinder}
import org.denigma.binding.extensions._
import org.denigma.binding.views.{BindableView, ItemsSeqView}
import org.opensciencce.school.lambda.domain._
import org.scalajs.dom._
import org.scalajs.dom.raw.HTMLElement
import rx.core.Var
import rx.ops._

import scala.collection.immutable.Seq

class DeviceView(val elem:HTMLElement,device:Var[Device], chosen:Var[Option[Device]]) extends BindableView{
  val params:Map[String,Any] = Map.empty
  val deviceName = device.map(d=>d.name)
  val port = device.map(d=>d.port)
  val select:Var[MouseEvent] = Var(Events.createMouseEvent())
  select.onChange("device_selection"){
    case ev=>
      chosen() = Some(device.now)
  }
}


class DevicesView(val elem:HTMLElement, val items:Var[Seq[Var[Device]]]) extends BindableView with ItemsSeqView {

  val chosen:Var[Option[Device]] = Var(None)

  override type Item = Var[Device]

  override type ItemView = DeviceView

  val empty = items.map(_.isEmpty)

  override def newItem(item: Var[Device]): DeviceView = this.constructItemView(item){
    case (el,mp)=>  new DeviceView(el,item,chosen).withBinder(new GeneralBinder(_))  }

}



