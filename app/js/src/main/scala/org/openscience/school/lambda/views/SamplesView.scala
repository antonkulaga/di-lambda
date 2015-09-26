package org.openscience.school.lambda.views

import org.denigma.binding.binders.{Events, GeneralBinder}
import org.denigma.binding.extensions._
import org.denigma.binding.views.{BindableView, ItemsSeqView}
import org.opensciencce.school.lambda.domain.Sample
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement
import rx.Rx
import rx.core.Var
import rx.ops._

import scala.collection.immutable.Seq
class SampleView(val elem:HTMLElement,val item:Var[Sample], val all:Var[Seq[Var[Sample]]]) extends BindableView{

  val currentData = Var(item.now)
  item.onChange("item_changed"){case i=> currentData.set(i)}

  val sampleName = Var(item.now.name)
  val description = Var(item.now.description)
  val changed = Rx{
    currentData()!=item()
  }

  val save = Var(Events.createMouseEvent())
  save.handler{
    item() = item()
  }

  val remove = Var(Events.createMouseEvent())
  remove.handler{
    all() = all.now.filterNot(i=>i==item)
  }
}

class SamplesView(val elem:HTMLElement, val items: Var[Seq[Var[Sample]]]) extends BindableView with ItemsSeqView {

  override type Item = Var[Sample]
  override type ItemView = SampleView

  val nameNew = Var("")
  val descriptionNew = Var("")
  val sampleNew = Rx{
    Sample(nameNew(),descriptionNew())
  }

  protected def clear() = {
    nameNew() = ""
    descriptionNew() = ""
  }

  val add = Var(Events.createMouseEvent())
  add.onChange("addClick"){
    case ev=>
      items() = items.now ++ List(Var(sampleNew.now))
      clear()
  }

  override def newItem(item: Item): SampleView =  this.constructItemView(item){
    case (el,args)=> new SampleView(el,item,items).withBinder(new GeneralBinder(_))
  }
  override lazy val injector = defaultInjector
    .register("suggest")    {
      case (el, args) => new SuggestView(el,items)
        .withBinder(  new GeneralBinder(_)  )}
}
