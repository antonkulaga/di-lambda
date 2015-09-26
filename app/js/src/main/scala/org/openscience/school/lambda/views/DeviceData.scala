package org.openscience.school.lambda.views

import org.denigma.binding.binders.GeneralBinder
import org.denigma.binding.views.{BindableView, ItemsSeqView}
import org.opensciencce.school.lambda.domain.DeviceData
import org.scalajs.dom.raw.HTMLElement
import rx.core.{Rx, Var}
import rx.ops._

import scala.collection.immutable.Seq

class RawDataView(val elem:HTMLElement,val items: Var[Seq[DeviceData]] ) extends BindableView with ItemsSeqView {

  override type Item = DeviceData

  override type ItemView = DeviceDataView

  override def newItem(item: Item): ItemView = this.constructItemView(item){
    case (el,args)=> new DeviceDataView(el,item).withBinder(new GeneralBinder(_))
  }

  val channels = items.map(its=>its.map(i=>i.channels))
  val averages = Rx{
    val size = channels.now.size
    val (one,two,three) = channels().foldLeft((0.0,0.0,0.0)){
      case ((a,b,c),el)=>
        (a + (if(el.isDefinedAt(0)) el.head else 0),b +(if(el.isDefinedAt(0)) el(1) else 0),c + (if(el.isDefinedAt(0)) el(2) else 0))
    }
    (one / size, two / size, three /size)
  }

  val avg1: rx.Rx[String] = averages.map(_._1.toString)
  val avg2 = averages.map(_._2.toString)
  val avg3 = averages.map(_._3.toString)

}
class DeviceDataView(val elem:HTMLElement, deviceData:DeviceData) extends BindableView {
  val device = Var(deviceData.device.name)
  val port = Var(deviceData.device.port)

  val chanels = Var(deviceData.channels.toList)
  val value1: rx.Rx[String] = chanels.map(v=>if(v.isDefinedAt(0)) v(0).toString else "N/A")
  val value2: rx.Rx[String] = chanels.map(v=>if(v.isDefinedAt(1)) v(1).toString else "N/A")
  val value3: rx.Rx[String] = chanels.map(v=>if(v.isDefinedAt(2)) v(2).toString else "N/A")

  val time = Var("")

}
