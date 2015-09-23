package org.openscience.school.lambda.views

import org.denigma.binding.binders.{Events, GeneralBinder}
import org.denigma.binding.views.{ItemsSeqView, BindableView}
import org.opensciencce.school.lambda.domain.{Sample, Value}
import org.scalajs.dom
import org.scalajs.dom.raw.{Event, HTMLElement}
import org.denigma.binding.extensions._
import rx.Rx
import rx.core.Var

import scala.collection.immutable.Seq
import rx.ops._
class SampleView(val elem:HTMLElement,val item:Var[Sample]) extends BindableView{

  val sampleName = item.map(i=>i.name)
  val description = item.map(_.description)

  val measure = Var(Events.createMouseEvent())
  measure.handler{
    dom.console.log("measure works!")
  }

  val remove = Var(Events.createMouseEvent())
  remove.handler{
    dom.console.log("removal works!")
  }
}

class Samples(val elem:HTMLElement) extends BindableView with ItemsSeqView {

  override type Item = Var[Sample]
  override type ItemView = SampleView
  override val items: Var[Seq[Item]] = Var(Seq(Var(Sample.blank)))

  val nameNew = Var("")
  val descriptionNew = Var("")
  val sampleNew = Rx{
    Sample(nameNew(),descriptionNew())
  }

  protected def clear() = {
    nameNew() = ""
    descriptionNew() = ""
  }

  val add = Var(Events.mousedown)
  add.onChange("addClick"){
    case ev=>
      items() = items.now ++ List(Var(sampleNew.now))
      clear()
  }

  override def newItem(item: Item): SampleView =  this.constructItemView(item){
    case (el,args)=> new SampleView(el,item).withBinder(new GeneralBinder(_))
  }
}
