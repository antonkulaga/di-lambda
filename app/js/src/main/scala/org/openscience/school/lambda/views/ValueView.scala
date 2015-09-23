package org.openscience.school.lambda.views

import org.denigma.binding.binders.GeneralBinder
import org.denigma.binding.views.{BindableView, ItemsSeqView}
import org.opensciencce.school.lambda.domain.Value
import org.scalajs.dom.raw.HTMLElement
import rx.core.{Rx, Var}
import rx.ops._

import scala.collection.immutable.Seq

class DataView(val elem:HTMLElement,val items: Var[Seq[Value]] ) extends BindableView with ItemsSeqView {

  override type Item = Value

  override type ItemView = ValueView

  override def newItem(item: Item): ItemView = this.constructItemView(item){
    case (el,args)=> new ValueView(el,item).withBinder(new GeneralBinder(_))
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
class ValueView(val elem:HTMLElement, value:Value) extends BindableView {
  val device = Var(value.device.name)
  val port = Var(value.device.port)

  val values = Var(value.channels.toList)
  val value1: rx.Rx[String] = values.map(v=>if(v.isDefinedAt(0)) v(0).toString else "N/A")
  val value2: rx.Rx[String] = values.map(v=>if(v.isDefinedAt(1)) v(1).toString else "N/A")
  val value3: rx.Rx[String] = values.map(v=>if(v.isDefinedAt(2)) v(2).toString else "N/A")

  val time = Var("")

}
