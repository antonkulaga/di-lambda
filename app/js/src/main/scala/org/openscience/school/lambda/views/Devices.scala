package org.openscience.school.lambda.views


import org.denigma.binding.binders.{Events, GeneralBinder}
import org.denigma.binding.views.{ItemsSeqView, ItemsSetView, CollectionView, BindableView}
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.raw.HTMLElement
import rx.Rx
import rx.core.Var

import scala.collection.immutable.Seq
import rx.ops._
import org.denigma.binding.extensions._

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

case class Device(name:String = "undefined",port:String)

class ValueView(val elem:HTMLElement, num:Double,val params:Map[String,Any] = Map.empty) extends BindableView {
  val value = Var(num.toString)
}

class DataView(val elem:HTMLElement,val params:Map[String,Any]) extends BindableView with ItemsSeqView {

  override type Item = Double

  override type ItemView = ValueView

  override def newItem(item: Item): ItemView = this.constructItemView(item){
    case (el,args)=> new ValueView(el,item).withBinder(new GeneralBinder(_))
  }

  override val items: Var[Seq[Item]] = Var(Seq.empty)

  private def test(something:Double):Unit = {
    val data = (for(i <- 0 until 4) yield 2000.0+scala.util.Random.nextInt(1000)).toSeq
    items() = data
    requestAnimationFrame(this.test _)
  }

  requestAnimationFrame(test _)
}

class Devices(val elem:HTMLElement,val params:Map[String,Any] = Map.empty) extends BindableView with ItemsSeqView {

  val chosen:Var[Option[Device]] = Var(None)

  override type Item = Var[Device]

  override type ItemView = DeviceView

  override val items: Var[Seq[Var[Device]]] = Var(Seq(
    Var(Device("SomeDevice1",port = "someport1")),
    Var(Device("SomeDevice2",port = "someport2"))
  ))

  override def newItem(item: Var[Device]): DeviceView = this.constructItemView(item){
    case (el,mp)=>  new DeviceView(el,item,chosen).withBinder(new GeneralBinder(_))  }

  override lazy val injector = this.defaultInjector.register("data"){
    case (el,args)=> new DataView(el,params).withBinder(new GeneralBinder(_))
  }
}
